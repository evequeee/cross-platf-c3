# PostgreSQL Port Conflict Diagnostic Script
Write-Host "=== PostgreSQL Port Conflict Diagnostic ===" -ForegroundColor Cyan
Write-Host ""

# 1. Check for local PostgreSQL Windows service
Write-Host "1. Checking for PostgreSQL Windows services..." -ForegroundColor Yellow
$pgServices = Get-Service -Name "*postgresql*" -ErrorAction SilentlyContinue
if ($pgServices) {
    Write-Host "   FOUND LOCAL POSTGRESQL SERVICE(S):" -ForegroundColor Red
    $pgServices | Format-Table Name, Status, DisplayName -AutoSize
} else {
    Write-Host "   No PostgreSQL Windows services found." -ForegroundColor Green
}

Write-Host ""

# 2. Check what's listening on port 5432
Write-Host "2. Checking what's listening on port 5432..." -ForegroundColor Yellow
$netstatOutput = netstat -ano | Select-String ":5432"
if ($netstatOutput) {
    Write-Host "   Processes using port 5432:" -ForegroundColor Cyan
    $netstatOutput | ForEach-Object { Write-Host "   $_" }
    
    # Extract PIDs and get process details
    $pids = $netstatOutput | ForEach-Object { 
        if ($_ -match '\s+(\d+)\s*$') { $matches[1] }
    } | Sort-Object -Unique
    
    Write-Host ""
    Write-Host "3. Process details for port 5432:" -ForegroundColor Yellow
    foreach ($pid in $pids) {
        if ($pid) {
            $proc = Get-Process -Id $pid -ErrorAction SilentlyContinue
            if ($proc) {
                Write-Host "   PID: $pid" -ForegroundColor Cyan
                Write-Host "   Name: $($proc.ProcessName)" -ForegroundColor White
                Write-Host "   Path: $($proc.Path)" -ForegroundColor White
                Write-Host ""
            }
        }
    }
} else {
    Write-Host "   Nothing listening on port 5432!" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Diagnostic Complete ===" -ForegroundColor Cyan
