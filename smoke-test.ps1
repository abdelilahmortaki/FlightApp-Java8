$ErrorActionPreference = "Stop"

$BaseUrl = "http://localhost:8080"
$Admin = "admin:admin123"
$Agent = "agent:agent123"
$Viewer = "viewer:viewer123"

$RunId = Get-Date -Format "yyyyMMddHHmmss"
$FlightNumber = "AF$RunId"

$TempDir = Join-Path $PWD "curl-test-$RunId"
New-Item -ItemType Directory -Force -Path $TempDir | Out-Null

$Passed = 0
$Failed = 0

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "=== $Message ===" -ForegroundColor Cyan
}

function Assert-Status {
    param(
        [string]$Name,
        [int]$Actual,
        [int[]]$Expected
    )

    if ($Expected -contains $Actual) {
        Write-Host "PASS [$Actual] $Name" -ForegroundColor Green
        $script:Passed++
    } else {
        Write-Host "FAIL [$Actual] $Name expected $($Expected -join ',')" -ForegroundColor Red
        $script:Failed++
    }
}

function Invoke-Curl {
    param(
        [string]$Name,
        [string[]]$CurlArgs,
        [int[]]$ExpectedStatus
    )

    $BodyFile = Join-Path $TempDir "$($Name -replace '[^a-zA-Z0-9_-]', '_').body.txt"
    $StatusFile = Join-Path $TempDir "$($Name -replace '[^a-zA-Z0-9_-]', '_').status.txt"

    $AllCurlArgs = @(
        "--silent",
        "--show-error",
        "--output", $BodyFile,
        "--write-out", "%{http_code}"
    )

    foreach ($item in $CurlArgs) {
        $AllCurlArgs += $item
    }

    Write-Host "curl.exe $($AllCurlArgs -join ' ')" -ForegroundColor DarkGray

    $Status = & curl.exe @AllCurlArgs
    Set-Content -Path $StatusFile -Value $Status

    $StatusCode = [int]$Status
    $Body = ""
    if (Test-Path $BodyFile) {
        $Body = Get-Content -Raw -Path $BodyFile
    }

    Assert-Status -Name $Name -Actual $StatusCode -Expected $ExpectedStatus

    return [PSCustomObject]@{
        Name = $Name
        Status = $StatusCode
        Body = $Body
        BodyFile = $BodyFile
    }
}

function To-JsonObject {
    param([string]$Body)
    if ([string]::IsNullOrWhiteSpace($Body)) {
        return $null
    }
    return $Body | ConvertFrom-Json
}

Write-Host "Starting full API validation against $BaseUrl"
Write-Host "Unique flight number: $FlightNumber"

Write-Step "1. Auth checks"

Invoke-Curl `
    -Name "no auth /api/me -> 401" `
    -CurlArgs @("$BaseUrl/api/me") `
    -ExpectedStatus @(401) | Out-Null

Invoke-Curl `
    -Name "bad password /api/me -> 401" `
    -CurlArgs @("-u", "admin:wrong", "$BaseUrl/api/me") `
    -ExpectedStatus @(401) | Out-Null

$AdminMe = Invoke-Curl `
    -Name "admin /api/me -> 200" `
    -CurlArgs @("-u", $Admin, "$BaseUrl/api/me") `
    -ExpectedStatus @(200)

$AgentMe = Invoke-Curl `
    -Name "agent /api/me -> 200" `
    -CurlArgs @("-u", $Agent, "$BaseUrl/api/me") `
    -ExpectedStatus @(200)

$ViewerMe = Invoke-Curl `
    -Name "viewer /api/me -> 200" `
    -CurlArgs @("-u", $Viewer, "$BaseUrl/api/me") `
    -ExpectedStatus @(200)

Write-Step "2. Create valid flight as admin"

$FlightFile = Join-Path $TempDir "flight-valid.json"
@"
{
  "flightNumber": "$FlightNumber",
  "originAirportCode": "CDG",
  "destinationAirportCode": "LIS",
  "departureTime": "2026-06-02T10:00:00",
  "arrivalTime": "2026-06-02T12:30:00",
  "capacity": 2
}
"@ | Set-Content -Encoding UTF8 $FlightFile

$CreateFlight = Invoke-Curl `
    -Name "admin create flight -> 200/201" `
    -CurlArgs @(
        "-u", $Admin,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/flights",
        "--data-binary", "@$FlightFile"
    ) `
    -ExpectedStatus @(200, 201)

$CreatedFlight = To-JsonObject $CreateFlight.Body
$FlightId = $CreatedFlight.id

if (-not $FlightId) {
    Write-Host "FAIL could not parse created flight id" -ForegroundColor Red
    Write-Host $CreateFlight.Body
    exit 1
}

Write-Host "Created flight id: $FlightId"

Write-Step "3. Flight security"

Invoke-Curl `
    -Name "viewer cannot create flight -> 403" `
    -CurlArgs @(
        "-u", $Viewer,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/flights",
        "--data-binary", "@$FlightFile"
    ) `
    -ExpectedStatus @(403) | Out-Null

Invoke-Curl `
    -Name "agent cannot create flight -> 403" `
    -CurlArgs @(
        "-u", $Agent,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/flights",
        "--data-binary", "@$FlightFile"
    ) `
    -ExpectedStatus @(403) | Out-Null

Write-Step "4. Flight business rules"

Invoke-Curl `
    -Name "duplicate flight number -> 409" `
    -CurlArgs @(
        "-u", $Admin,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/flights",
        "--data-binary", "@$FlightFile"
    ) `
    -ExpectedStatus @(409) | Out-Null

$SameAirportFile = Join-Path $TempDir "flight-same-airport.json"
@"
{
  "flightNumber": "$FlightNumber-SAME",
  "originAirportCode": "CDG",
  "destinationAirportCode": "CDG",
  "departureTime": "2026-06-02T10:00:00",
  "arrivalTime": "2026-06-02T12:30:00",
  "capacity": 2
}
"@ | Set-Content -Encoding UTF8 $SameAirportFile

Invoke-Curl `
    -Name "same origin destination -> 409" `
    -CurlArgs @(
        "-u", $Admin,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/flights",
        "--data-binary", "@$SameAirportFile"
    ) `
    -ExpectedStatus @(409) | Out-Null

$BadTimeFile = Join-Path $TempDir "flight-bad-time.json"
@"
{
  "flightNumber": "$FlightNumber-BADTIME",
  "originAirportCode": "CDG",
  "destinationAirportCode": "LIS",
  "departureTime": "2026-06-02T14:00:00",
  "arrivalTime": "2026-06-02T12:30:00",
  "capacity": 2
}
"@ | Set-Content -Encoding UTF8 $BadTimeFile

Invoke-Curl `
    -Name "departure after arrival -> 409" `
    -CurlArgs @(
        "-u", $Admin,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/flights",
        "--data-binary", "@$BadTimeFile"
    ) `
    -ExpectedStatus @(409) | Out-Null

$BadCapacityFile = Join-Path $TempDir "flight-bad-capacity.json"
@"
{
  "flightNumber": "$FlightNumber-BADCAP",
  "originAirportCode": "CDG",
  "destinationAirportCode": "LIS",
  "departureTime": "2026-06-02T10:00:00",
  "arrivalTime": "2026-06-02T12:30:00",
  "capacity": 0
}
"@ | Set-Content -Encoding UTF8 $BadCapacityFile

Invoke-Curl `
    -Name "bad capacity -> 400" `
    -CurlArgs @(
        "-u", $Admin,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/flights",
        "--data-binary", "@$BadCapacityFile"
    ) `
    -ExpectedStatus @(400) | Out-Null

Write-Step "5. Flight read endpoints"

Invoke-Curl `
    -Name "viewer get created flight by id -> 200" `
    -CurlArgs @("-u", $Viewer, "$BaseUrl/api/flights/$FlightId") `
    -ExpectedStatus @(200) | Out-Null

Invoke-Curl `
    -Name "viewer get paginated flights -> 200" `
    -CurlArgs @("-u", $Viewer, "$BaseUrl/api/flights?page=0&size=5") `
    -ExpectedStatus @(200) | Out-Null

Write-Step "6. Booking success and seat decrement"

$BookingOneFile = Join-Path $TempDir "booking-one.json"
@"
{
  "flightId": $FlightId,
  "passengerName": "Passenger One",
  "passengerEmail": "passenger.one.$RunId@example.com"
}
"@ | Set-Content -Encoding UTF8 $BookingOneFile

$BookingOne = Invoke-Curl `
    -Name "agent creates booking one -> 200/201" `
    -CurlArgs @(
        "-u", $Agent,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/bookings",
        "--data-binary", "@$BookingOneFile"
    ) `
    -ExpectedStatus @(200, 201)

$BookingOneJson = To-JsonObject $BookingOne.Body
$BookingOneId = $BookingOneJson.id

if (-not $BookingOneId) {
    Write-Host "FAIL could not parse booking one id" -ForegroundColor Red
    Write-Host $BookingOne.Body
    exit 1
}

Write-Host "Created booking one id: $BookingOneId"

$AfterBookingOne = Invoke-Curl `
    -Name "get flight after one booking -> 200" `
    -CurlArgs @("-u", $Viewer, "$BaseUrl/api/flights/$FlightId") `
    -ExpectedStatus @(200)

$AfterBookingOneJson = To-JsonObject $AfterBookingOne.Body
if ($AfterBookingOneJson.availableSeats -eq 1) {
    Write-Host "PASS availableSeats decreased to 1" -ForegroundColor Green
    $Passed++
} else {
    Write-Host "FAIL availableSeats expected 1 actual $($AfterBookingOneJson.availableSeats)" -ForegroundColor Red
    $Failed++
}

Write-Step "7. Duplicate booking should fail"

Invoke-Curl `
    -Name "duplicate passenger same flight -> 409" `
    -CurlArgs @(
        "-u", $Agent,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/bookings",
        "--data-binary", "@$BookingOneFile"
    ) `
    -ExpectedStatus @(409) | Out-Null

Write-Step "8. Fill flight and test full-flight rule"

$BookingTwoFile = Join-Path $TempDir "booking-two.json"
@"
{
  "flightId": $FlightId,
  "passengerName": "Passenger Two",
  "passengerEmail": "passenger.two.$RunId@example.com"
}
"@ | Set-Content -Encoding UTF8 $BookingTwoFile

$BookingTwo = Invoke-Curl `
    -Name "agent creates booking two -> 200/201" `
    -CurlArgs @(
        "-u", $Agent,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/bookings",
        "--data-binary", "@$BookingTwoFile"
    ) `
    -ExpectedStatus @(200, 201)

$BookingTwoJson = To-JsonObject $BookingTwo.Body
$BookingTwoId = $BookingTwoJson.id

$BookingThreeFile = Join-Path $TempDir "booking-three.json"
@"
{
  "flightId": $FlightId,
  "passengerName": "Passenger Three",
  "passengerEmail": "passenger.three.$RunId@example.com"
}
"@ | Set-Content -Encoding UTF8 $BookingThreeFile

Invoke-Curl `
    -Name "third booking on capacity 2 flight -> 409" `
    -CurlArgs @(
        "-u", $Agent,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/bookings",
        "--data-binary", "@$BookingThreeFile"
    ) `
    -ExpectedStatus @(409) | Out-Null

Write-Step "9. Booking security"

Invoke-Curl `
    -Name "viewer cannot create booking -> 403" `
    -CurlArgs @(
        "-u", $Viewer,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/bookings",
        "--data-binary", "@$BookingThreeFile"
    ) `
    -ExpectedStatus @(403) | Out-Null

Write-Step "10. Cancel booking and restore seat"

Invoke-Curl `
    -Name "agent cancels booking one -> 200" `
    -CurlArgs @(
        "-u", $Agent,
        "-X", "PATCH",
        "$BaseUrl/api/bookings/$BookingOneId/cancel"
    ) `
    -ExpectedStatus @(200) | Out-Null

$AfterCancel = Invoke-Curl `
    -Name "get flight after cancel -> 200" `
    -CurlArgs @("-u", $Viewer, "$BaseUrl/api/flights/$FlightId") `
    -ExpectedStatus @(200)

$AfterCancelJson = To-JsonObject $AfterCancel.Body
if ($AfterCancelJson.availableSeats -eq 1) {
    Write-Host "PASS availableSeats restored to 1 after cancelling one of two bookings" -ForegroundColor Green
    $Passed++
} else {
    Write-Host "FAIL availableSeats expected 1 after cancel actual $($AfterCancelJson.availableSeats)" -ForegroundColor Red
    $Failed++
}

Invoke-Curl `
    -Name "cancel already cancelled booking -> 409" `
    -CurlArgs @(
        "-u", $Agent,
        "-X", "PATCH",
        "$BaseUrl/api/bookings/$BookingOneId/cancel"
    ) `
    -ExpectedStatus @(409) | Out-Null

Write-Step "11. Flight status and no booking on cancelled flight"

Invoke-Curl `
    -Name "admin cancels flight -> 200" `
    -CurlArgs @(
        "-u", $Admin,
        "-X", "PATCH",
        "$BaseUrl/api/flights/$FlightId/cancel"
    ) `
    -ExpectedStatus @(200) | Out-Null

$BookingAfterFlightCancelFile = Join-Path $TempDir "booking-after-flight-cancel.json"
@"
{
  "flightId": $FlightId,
  "passengerName": "Passenger Four",
  "passengerEmail": "passenger.four.$RunId@example.com"
}
"@ | Set-Content -Encoding UTF8 $BookingAfterFlightCancelFile

Invoke-Curl `
    -Name "cannot book cancelled flight -> 409" `
    -CurlArgs @(
        "-u", $Agent,
        "-H", "Content-Type: application/json",
        "-X", "POST",
        "$BaseUrl/api/bookings",
        "--data-binary", "@$BookingAfterFlightCancelFile"
    ) `
    -ExpectedStatus @(409) | Out-Null

Write-Step "12. Batch endpoints"

Invoke-Curl `
    -Name "admin lists batch jobs -> 200" `
    -CurlArgs @("-u", $Admin, "$BaseUrl/api/batch/jobs") `
    -ExpectedStatus @(200) | Out-Null

Invoke-Curl `
    -Name "agent cannot list batch jobs -> 403" `
    -CurlArgs @("-u", $Agent, "$BaseUrl/api/batch/jobs") `
    -ExpectedStatus @(403) | Out-Null

Invoke-Curl `
    -Name "admin launches flightImportJob -> 200" `
    -CurlArgs @(
        "-u", $Admin,
        "-X", "POST",
        "$BaseUrl/api/batch/jobs/flightImportJob/launch"
    ) `
    -ExpectedStatus @(200) | Out-Null

Invoke-Curl `
    -Name "agent cannot launch flightImportJob -> 403" `
    -CurlArgs @(
        "-u", $Agent,
        "-X", "POST",
        "$BaseUrl/api/batch/jobs/flightImportJob/launch"
    ) `
    -ExpectedStatus @(403) | Out-Null

Invoke-Curl `
    -Name "admin sees flightImportJob executions -> 200" `
    -CurlArgs @("-u", $Admin, "$BaseUrl/api/batch/jobs/flightImportJob/executions") `
    -ExpectedStatus @(200) | Out-Null

Write-Step "13. Optional actuator behavior"

Invoke-Curl `
    -Name "actuator health protected or available -> 200/401" `
    -CurlArgs @("$BaseUrl/actuator/health") `
    -ExpectedStatus @(200, 401) | Out-Null

Write-Host ""
Write-Host "====================================="
Write-Host "Test summary"
Write-Host "Passed: $Passed" -ForegroundColor Green
Write-Host "Failed: $Failed" -ForegroundColor $(if ($Failed -eq 0) { "Green" } else { "Red" })
Write-Host "Temp files: $TempDir"
Write-Host "====================================="

if ($Failed -gt 0) {
    exit 1
}

exit 0
