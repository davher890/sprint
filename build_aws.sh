gradle clean build -x test
eb use Sprint-Api-env  --profile davher890
eb deploy --profile davher890 --staged