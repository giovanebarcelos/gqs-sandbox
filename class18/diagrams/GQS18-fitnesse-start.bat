@echo off
rem Sobe o FitNesse com o workspace da Aula 18 (Windows). Rode a partir de repository\class18\fitnesse
cd /d "%~dp0..\fitnesse"
if not exist classes mkdir classes
javac -encoding UTF-8 -d classes src\*.java
java -jar fitnesse.jar -p 8080 -d .
