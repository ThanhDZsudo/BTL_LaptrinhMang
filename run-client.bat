@echo off
chcp 65001 > nul
title RMI CLIENT - QUAN LY TIN TUC
echo ==================================================
echo         DANG KHOI CHAY RMI CLIENT...
echo ==================================================
java -Dfile.encoding=UTF-8 -cp "bin;lib/*" client.ClientMain
pause

