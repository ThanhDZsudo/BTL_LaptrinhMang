@echo off
chcp 65001 > nul
title RMI SERVER - QUAN LY TIN TUC
echo ==================================================
echo         DANG KHOI CHAY RMI SERVER...
echo ==================================================
java -Dfile.encoding=UTF-8 -cp "bin;lib/*" server.ServerMain
pause

