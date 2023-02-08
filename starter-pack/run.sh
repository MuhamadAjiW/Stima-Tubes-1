#!/bin/bash
# Main Bot:
# cd ./starter-bots/JavaBot/target && sleep 3 && java -jar JavaBot.jar &
# Bot sparring:
# Lv0: cd ./reference-bot-publish/ && sleep 3 && dotnet ReferenceBot.dll &
# Lv1: cd ./dummyBots/Dummy1/ && sleep 3 && java -jar Dummy1.jar &
# Lv2: cd ./dummyBots/Dummy2/ && sleep 3 && java -jar Dummy2.jar &

cd ./runner-publish/ && dotnet GameRunner.dll &
cd ./engine-publish/ && sleep 1 && dotnet Engine.dll &
cd ./logger-publish/ && sleep 1 && dotnet Logger.dll &
cd ./dummyBots/Dummy2/ && sleep 3 && java -jar Dummy2.jar &
cd ./dummyBots/Dummy2/ && sleep 3 && java -jar Dummy2.jar &
cd ./dummyBots/Dummy2/ && sleep 3 && java -jar Dummy2.jar &

wait