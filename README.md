**NOTE**<br />
*This project was made as an assignment. Below there is a brief description of the assignment.<br>
The PDF document containing full description along with the related implementation requirements can be downloaded from my [Google Drive](https://drive.google.com/file/d/0BxGozyk59-gGRVJRSzVqTkY0ajA/edit?usp=sharing).*

----

## Brief description of the Java assignment

### Service
The goal is to prepare an application that periodically queries data from the network, converts that data to a suitable format and serves it up as a web service in JSON format. In order to obtain the source data, one needs to query a “legacy” data interface, located at http://people.proekspert.ee/ak/. The request format for the interface is “data_n.txt”
(wherein n is replaced by a number between 1-9).

Sample request: http://people.proekspert.ee/ak/data_1.txt

Request into the legacy interface has to be made periodically every 5 seconds, in an ascending order according to the request parameter index (1, 2, 3, …). The generated web service should only return actual data from the legacy interface (data from the previous files should not be buffered).

**Reply format and specification**

Data is “service parameters“, provided as positional text:

`**ABBB..CDEFFF..**`, whereas<br>
**A:** ‘A’ active / ’P’ passive (1 symbol)<br>
**BBB..:** phone number (20 symbols)<br>
**C:** XL-additional service, ‘J’ yes / ’E’ no (1 symbol)<br>
**D:** language E=Estonian, I=English (1 symbol)<br>
**E:** XL-additional service language (1 symbol)<br>
**FFF...:** service end date (8 symbols, format YYYYMMDD)<br>
**GGG...:** service end time (ttmm) (4 symbols)<br>
**HHH...:** XL service activation time 1 (ttmm) (4 symbols)<br>
**III...:** XL service end time (ttmm) (4 symbols)<br>
**J:** override list, ‘K’ in use / ’E’ not in use (1 symbol)<br>
**8\*KKK..:** phone numbers (15 symbols \* 8 = 120 symbols)<br>
**8\*LLL..:** names (20 symbols \* 8 = 160 symbols)<br />


<br>

### Client
The goal is to prepare an HTML page that periodically reads data from the service created by you using AJAX and displays them on the screen. The following items should be displayed regarding the service:
 1. Request sequence number
 2. Associated phone number (from parameter B)
 3. Whether the service is active (from parameter A, the rest of the parameters should only be displayed in case the service is active)
 4. When does the service become inactive (from parameters F and G)
 5. Service language (from parameter D)
 6. XL-service state and activity period (from parameters C, E, H, I; activity period and language should only be displayed in case the service is active)
 7. The list of phone numbers and names contained in the override list (J, K, L; the override list should be displayed only in case an override list is in use)
