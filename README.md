Database Controller


Files:
- Historic
- Sport Schools
	- Santa Ana
	- Paracuellos
	- Externos


Historic

I receive by email the Excel file.
First upload the file to Google Drive.
Download it in CSV Format
Check the csv file and look for line breaks.

Endpoint: /database/historic

Sport Schools

I receive by email the Excel file. The password should be in the mail.
Create new excel file in Google Drive.
Copy the content from the local file to the Google Drive one.
Download it in CSV Format
Check the csv file and look for line breaks. Observations field eventually contains line breaks.


Endpoints: /database/{id}/athletes
	- Santa Ana: id = 1
	- Paracuellos: id = 2
	- Externos: id = externo