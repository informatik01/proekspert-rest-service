# Java REST web service (JAX-RS) and AJAX web client. #

This project is a Java assignment. Below there is a brief description of the assignment.<br>
The PDF document containing full description along with the related implementation requirements can be downloaded from my <a href='https://drive.google.com/file/d/0BxGozyk59-gGRVJRSzVqTkY0ajA/edit?usp=sharing'>Google Drive</a>.<br>
<br>
<b>IMPLEMENTATION NOTE:</b><br>
The project is implemented utilizing Cross-Origin Resource Sharing (<a href='http://en.wikipedia.org/wiki/Cross-origin_resource_sharing'>CORS</a>).<br>
<hr />
<h2>Brief description of the Java assignment</h2>

<h3><u>Service</u></h3>
The goal is to prepare an application that periodically queries data from the network, converts that data to a suitable format and serves it up as a web service in JSON format. In order to obtain the source data, one needs to query a “legacy” data interface, located at <a href='http://people.proekspert.ee/ak/'>http://people.proekspert.ee/ak/</a>. The request format for the interface is “data_n.txt”<br>
(wherein n is replaced by a number between 1-9).<br>
<br>
Sample request: <a href='http://people.proekspert.ee/ak/data_1.txt'>http://people.proekspert.ee/ak/data_1.txt</a>

Request into the legacy interface has to be made periodically every 5 seconds, in an ascending order according to the request parameter index (1, 2, 3, …). The generated web service should only return actual data from the legacy interface (data from the previous files should not be buffered).<br>
<br>
<h3>Reply format and specification</h3>

Data is “service parameters“, provided as positional text:<br>
<br>
<b>ABBB..CDEFFF..</b>, whereas<br>
<b>A:</b> ‘A’ active / ’P’ passive (1 symbol)<br>
<b>BBB..:</b> phone number (20 symbols)<br>
<b>C:</b> XL-additional service, ‘J’ yes / ’E’ no (1 symbol)<br>
<b>D:</b> language E=Estonian, I=English (1 symbol)<br>
<b>E:</b> XL-additional service language (1 symbol)<br>
<b>FFF...:</b> service end date (8 symbols, format YYYYMMDD)<br>
<b>GGG...:</b> service end time (ttmm) (4 symbols)<br>
<b>HHH...:</b> XL service activation time 1 (ttmm) (4 symbols)<br>
<b>III...:</b> XL service end time (ttmm) (4 symbols)<br>
<b>J:</b> override list, ‘K’ in use / ’E’ not in use (1 symbol)<br>
<b>8<code>*</code>KKK..:</b> phone numbers (15 symbols <code>*</code> 8 = 120 symbols)<br>
<b>8<code>*</code>LLL..:</b> names (20 symbols <code>*</code> 8 = 160 symbols)<br />

<h3><u>Client</u></h3>
The goal is to prepare an HTML page that periodically reads data from the service created by you using AJAX and displays them on the screen. The following items should be displayed regarding the service:<br>
<ol><li>Request sequence number<br>
</li><li>Associated phone number (from parameter B)<br>
</li><li>Whether the service is active (from parameter A, the rest of the parameters should only be displayed in case the service is active)<br>
</li><li>When does the service become inactive (from parameters F and G)<br>
</li><li>Service language (from parameter D)<br>
</li><li>XL-service state and activity period (from parameters C, E, H, I; activity period and language should only be displayed in case the service is active)<br>
</li><li>The list of phone numbers and names contained in the override list (J, K, L; the override list should be displayed only in case an override list is in use)