# Заготовленные тесты

## Тесты аргумента vfs
"test vfs.bat" - задаёт расположение VFS в текущей директории
"test vfs 2.bat" - задаёт расположение VFS в C:\users

## Тесты аргумента script
"test script.bat" - запускает скрипт test.script в текущей директории
"test script 2.bat" - запускает скрипт test.script из предыдущей директории (..)

## Тесты с 2 аргументами
"test 2 args.bat" - объединяет "test vfs.bat" и "test script.bat"
"test 2 args 2.bat" - объединяет "test vfs 2.bat" и "test script 2.bat"

## Тест с многими аргументами
"test 4 args.bat" - объединяет "test 2 args.bat" и "test 2 args 2.bat"

## Тесты с ошибками
"test error.bat" - задаёт несуществующее расположение VFS и запускает несуществующий скрипт
"test wrong arg" - передаёт несуществующий аргумент
