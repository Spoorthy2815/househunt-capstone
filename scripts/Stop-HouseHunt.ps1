$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$processes = @(Get-CimInstance Win32_Process | Where-Object { $_.Name -in 'node.exe','java.exe' })
$targets = @()
foreach ($process in $processes) {
    $command = [string]$process.CommandLine
    $isFrontend = $process.Name -eq 'node.exe' -and $command -match 'vite[\\/]bin[\\/]vite.js'
    $isBackend = $process.Name -eq 'java.exe' -and $command -match 'HouseHuntApplication|househunt-1\.0\.0\.jar|spring-boot:run'
    if (-not ($isFrontend -or $isBackend)) { continue }
    $belongsToProject = $command.IndexOf($projectRoot, [StringComparison]::OrdinalIgnoreCase) -ge 0
    if (-not $belongsToProject -and $isBackend) {
        $argFileMatch = [regex]::Match($command, '@(?:"([^"]+)"|([^\s]+))')
        if ($argFileMatch.Success) {
            $argFile = if ($argFileMatch.Groups[1].Success) { $argFileMatch.Groups[1].Value } else { $argFileMatch.Groups[2].Value }
            if (Test-Path -LiteralPath $argFile) {
                $arguments = Get-Content -LiteralPath $argFile -Raw
                $belongsToProject = $arguments.IndexOf($projectRoot, [StringComparison]::OrdinalIgnoreCase) -ge 0
            }
        }
    }
    if ($belongsToProject) { $targets += $process }
}
# Stop application children before their Maven launchers.
$targets = $targets | Sort-Object @{Expression={if ($_.CommandLine -match 'spring-boot:run') {1} else {0}}}
foreach ($process in $targets) {
    Stop-Process -Id $process.ProcessId -ErrorAction SilentlyContinue
    Write-Host "Stopped HouseHunt process $($process.ProcessId)."
}
if (-not $targets) { Write-Host 'No HouseHunt processes are running.' }