# Bills

Batch application that uses OCR to get bank receipt image files, process and generate a csv.


### Build
```
mvn compile dependency:copy-dependencies -DincludeScope=runtime
```

### Run with docker
```
docker build ./ -t 'bills'

docker run -e AWS_ACCESS_KEY_ID={access key} -e AWS_SECRET_ACCESS_KEY={secret key} -p 8080:8080 bills

curl "http://localhost:8080/2015-03-31/functions/function/invocations" -d '{"payload":"hello world!"}'   
```
