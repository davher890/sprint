gradle clean build -x test
eb use Sprintapi-env-1  --profile davher890
eb deploy --profile davher890 --staged