Video Upload POC
===============


Test URLS
-----------

* [Server Side] - http://159.203.192.45:8080/
* [username/password] - api/api100
* [SPA] - http://159.203.192.45/vz/
* [ASSETS] -   https://exsracing.com/vz/ASSETS/
* [ES6-Promise] - A polyfill for ES6-style Promises




To Build and Run the project
-----------------------------
```sh
 git clone  https://github.com/gskudlarick/videouploads.git
 ```
 * run the sql.ddl in src/main/resources
 * update application.properties file for db connection and directories.
 * mvn clean package
 * java -jar target/gs-securing-web-0.1.0.ja 
 * Test at localhost:8080
 
 
 ## Features to Implement
 - [x] **Clean UI with bootstrap**
 - [ ] **Add File Upload Progress Bar** *Technique: Add 2nd Servlet/Rest Client with Progress info for Ajax Calls*
  * Use Apache Commons FileUpload package
  * Implement ProgressListener and override update() with a  HttpSession attribute with the status %
  * Create a new Rest endpoint to return the % value in the HttpSession attribute
  * Create a JavaScript XMLHttpRequest ajax call on window.setInterva(2000)
    * implement callback method to get  % from Restpoint
    * Update the % on a bootstrap DOM progress bar element.
    * Try the newer XHR. ProgressEvent onProgress() and see what kind of data it sends back for multi-part
 - [ ] **Figure out how to Stream the File Upload multi-part Stream to the ffprobe input**
  * This way we don't have to Upload the file to run ffprobe and get the duration info.
    * Shortcut would be to limit file size using **spring.http.multipart.max-request-size=128MB**
   * Break out the FileUpload Steam to somehow write to thie new Stream opened by ffprobe
   * Figure out how to  configure ffprobe to read from a STDIN File Descripter, and get Java to wrote to it.
  - [ ] **Add swagger Documentation, and JavaDoc**
 - [ ] **Update Security**
    * Currrently uses Basic Authentication.  Modify to form based using JWT tokens
     * Currently does not encrypt password. Add Login Form and use Use *BCryptPasswordEncoder()*.
     * Update code in *WebSecurityConfig* to update  *config(HttpSecurity)...* and *CorsFilter.java*
     * Test and ensure CORS, CSRF working locked down for Rest Clients.  
 


