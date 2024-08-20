sdk use java 17-zulu
sdk use gradle 7.3
gradle clean build -x test
eb deploy Sprint-Api-env --profile davher890 --staged