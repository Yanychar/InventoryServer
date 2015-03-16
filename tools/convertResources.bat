@echo off


set source=C:\Users\sevastia\workspace_tms


set jdk_home="C:\Program Files\Java\jdk1.8.0_31"
set source="D:\Development\Inventory Development\workspace_InventoryServer\InventoryServer"
set dest="D:\Development\Inventory Development\workspace_InventoryServer\InventoryServer\src\com\c2point\tools\ui\resources"


del %dest%\WebResources_en_FI.properties
del %dest%\WebResources_fi_FI.properties
del %dest%\WebResources_et_FI.properties
del %dest%\WebResources_ru_FI.properties


%jdk_home%\bin\native2ascii -encoding utf8 %source%\WebResources_en_FI.properties %dest%\WebResources_en_FI.properties
%jdk_home%\bin\native2ascii -encoding utf8 %source%\WebResources_fi_FI.properties %dest%\WebResources_fi_FI.properties
%jdk_home%\bin\native2ascii -encoding utf8 %source%\WebResources_et_FI.properties %dest%\WebResources_et_FI.properties
%jdk_home%\bin\native2ascii -encoding utf8 %source%\WebResources_ru_FI.properties %dest%\WebResources_ru_FI.properties


REM del %source%\resources.csv

REM del %source%\WebResources_en_FI.properties
REM del %source%\WebResources_fi_FI.properties
REM del %source%\WebResources_et_FI.properties
REM del %source%\WebResources_ru_FI.properties



pause
