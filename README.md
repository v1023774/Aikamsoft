# Aikamsoft
## Тестовое задание.
### Использованные технологии.
1. Java 8
2. Maven
3. Postgresql
4. Gson
5. ORM hibernate

## Инструкция по запуску программы.
1. В папке проекта перейдите в папку  out\artifacts\Aikamsoft_jar. 
2. Здесь вы можете запустить Aikamsoft.jar файл командой : java -jar .\Aikamsoft.jar search ..\\..\\..\\inputdir\\inputsearch.json ..\\..\\..\\outputdir\\output.json.
После указания имени jar файла нужно указать 3 параметра: 1 - критерий(search или stat)б 2 - путь входного json файла, 3 - путь выходного json файла.

## Инструкция по восстановлению базы данных.
1. В папке проекта в папке database находится файл Aikamsoftdump.sql. 
2. Чтобы восстановить базу данных введите в командной строке:  psql -U username -d Aikamsoft < Aikamsoftdump.sql.
