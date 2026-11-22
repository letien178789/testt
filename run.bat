
@echo off
chcp 65001 >nul

echo ==================================
echo   INVENTORY MANAGEMENT - RUN
echo ==================================

powershell -NoProfile -ExecutionPolicy Bypass -Command "$files = Get-ChildItem -Recurse -Filter *.java | Where-Object { $_.FullName -notmatch '\\.git\\' } | ForEach-Object { $_.FullName }; if (-not $files) { Write-Host 'No Java files found'; exit 1 }; javac -encoding UTF-8 -d . $files; if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }; java -Dfile.encoding=UTF-8 -cp . present.Main"

echo.
pause
