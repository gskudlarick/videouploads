# Video Upload POC



## Test URLS


* **Server Side Rendering** - http://159.203.192.45:8080/
* **username/password** - *api  api100*
* **Angular Client**- http://159.203.192.45/vz/
* **UPLOADED ASSETS** -   https://exsracing.com/vz/ASSETS/


## To Build and Run the project

```sh
 git clone  https://github.com/gskudlarick/videouploads.git
 ```
 * Run the *sql.ddl* in src/main/resources
 * Update *application.properties* file for db connection and directories.
 ```
 mvn clean package
 java -jar target/gs-securing-web-0.1.0.ja 
  ```
 * Test at   __http://localhost:8080__
 
 
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
 - [ ] **Update UI Video Player to Play Uploaded Videos and Thumbs**
   * I Customized the json output from the transcoding process to work with videojs-thumbnails plugin. https://www.npmjs.com/package/videojs-thumbnails
   * e.g. https://exsracing.com/vz/ASSETS/trailer_thumbs.json
   * I have this working on another site. http://testplayer.crackle.com/thumbs/chucky2.html **Test the Scrubbing**
   * It would take me about 4 hours to add some HTML Drop Down controls to Select from uploaded Videos and implement on the test Site.

## Some Sample Data Results from Running the Upload

### Rest API Call of Video Upload Data
 *  **Restful Client**  *Select Admin & Test Tab*  *http://159.203.192.45/vz*
*  **Restful API Call**  *http://159.203.192.45:8080/videos*
```
[
    {
        "mediaId": 1,
        "format": {
            "filename": "upload-dir/605.mp4",
            "duration": 254.875,
            "size": 2906430,
            "format_name": "mov,mp4,m4a,3gp,3g2,mj2",
            "bit_rate": 91226
        },
        "videourls": {
            "videoUrl": "https://exsracing.com/vz/ASSETS/605.mp4",
            "videoSpriteUrl": "https://exsracing.com/vz/ASSETS/605_sprite.jpg",
            "videoThumbsJsonUrl": "https://exsracing.com/vz/ASSETS/605_thumbs.json",
            "videoPosterUrl": "https://exsracing.com/vz/ASSETS/605_poster.jpg",
            "videoHlsUrl": "https://exsracing.com/vz/ASSETS/HLS/605.m3u8",
            "videoMpdUrl": "https://exsracing.com/vz/ASSETS/MPD/-TBD-605.mpd"
        },
        "name": "greg test",
        "description": "test 101",
        "metaData": null,
        "author": "api",
        "authorIpAddress": "104.173.248.94",
        "authorLocation": null,
        "uploadDate": null,
        "createDate": 1509455800000,
        "modifyDate": 1509455800000,
        "file": null
    }
]
```


### JSON for Custom Thumbnmail processing.  
**Specifies the paramaters of which section of the Sprite to show for video time interval**, e.g. 0, 10, 20, 
```json  
   {

  "0": {
    "src": "trailer_sprite.jpg",
    "style": {
      "width": "300px",
      "height": "168px",
      "left": "-50px",
      "top": "-66px",
      "clip": "rect(0px, 100px, 56px, 0px)"
    }
  },
  "10": {
    "style": {
      "left": "-150px",
      "top": "-66px",
      "clip": "rect(0px, 200px, 56px, 100px)"
    }
  },
    "20": {
    "style": {
      "left": "-250px",
      "top": "-66px",
      "clip": "rect(0px, 300px, 56px, 200px)"
    }
  }
 }
  
  ```
 


