# Video Upload POC

![alt text](https://help.github.com/assets/images/site/be-social.gif)

## Test URLS


* **Server Side Rendering** - http://159.203.192.45:8080/
* **username/password** - *api  api100*
* **Angular Client**- http://159.203.192.45/vz/ 
* **UPLOADED ASSETS** -       
  * My DigitalOcean Server. I Didnt'have tiem to set up AWS S3*
  * https://exsracing.com/vz/ASSETS/
  *   https://exsracing.com/vz/ASSETS/HLS/


## To Build and Run the project

* In a terminal window:.
``` sh
  >mkdir my-proj
  >git clone  https://github.com/gskudlarick/videouploads.git
  >cd my-proj
```
 * Setup mysql db.   https://dev.mysql.com/doc/mysql-getting-started/en/#mysql-getting-started-installing
   * Install mamp for quick setup:  https://www.mamp.info/en/
 * Run the *sql.ddl* in src/main/resources
 * Update *application.properties* file for withyou db connection info and directories structure
  ``` sh
    spring.datasource.url=jdbc:mysql://localhost:8889/my_db
    spring.datasource.username=un
    spring.datasource.password=pw
    api.config.uploaddir=upload-dir
    api.config.ffprobe.bin=/usr/bin/ffprobe
    api.config.ffmpeg.bin=/usr/bin/ffmpeg
    api.config.python.bin=/usr/bin/python
    transcoding.upload.dir=upload-dir/
    transcoding.pipeline.processing.list=_sprite.jpg,_thumbs.json
    transcoding.pipeline.asset.list=_sprite.jpg,_thumbs.json,.mp4
    ...  See complete list at bottom of readme.
 ```   
 * Build and run the project:
``` sh
  >mvn clean package
  >java -jar target/gs-securing-web-0.1.0.java
```
 * Test at   __http://localhost:8080__
 
 
 ## Features to Implement
 - [x] **Clean UI with bootstrap**
  - [ ] **Refactor Java Code**
   * Re- Organize File Structure.  Make File Copy into Reusable Service,  Add Proptreis for Messages.
 - [ ] **Add File Upload Progress Bar** *Technique: Add 2nd Servlet/Rest Client with Progress info for Ajax Calls*
  * **Technique 1 Progress Endpoint** Implement ProgressListener and override update() with a  HttpSession attribute with the status %
    * Use Apache Commons FileUpload package
    * Create a new Rest endpoint to return the % value in the HttpSession attribute
    * Create a JavaScript XMLHttpRequest ajax call on window.setInterva(2000)
    * implement callback method to get  % from Restpoint
    * Update the % on a bootstrap DOM progress bar element.
    * Try the newer XHR. ProgressEvent onProgress() and see what kind of data it sends back for multi-part
  * **Technique 2 Web Sockets** Use Java API for WebSocket (JSR 356) to communicate with JavaScript Client Pop up
    * Similar to chat implementation.  Use JavaScript WebSockets Api on client side.  Jquery Popup, or Angular.
 * **Technique 3 Job/Workflow Service** Create Data driven Job Service.  
    *  **Scales for large jobs**, which can run in background,  then update client status via messaging. (email, txt, websocket etc.) 
    * Server updates jobs_tbl, and workflow_table in DB.  Uses workflow_id, job_id.
    * File Upload Controller creates a new job for Video Prcessing.
    * File Upload Controler creates a Workflow with the following steps:  
      1.  file upload 
      2.  ffprobe get file status 
      3.  thumbs generation  
      4.  poster generation
      5.  HLS processing
    * Server updates status via WebSocket API
    * Client uses Javascript WebSockets API, and updaet widget, with progress bars for each step in the workflow
    
 - [ ] **Figure out how to pipe the File Upload multi-part Stream to the ffprobe input**
  * This way we don't have to Upload the file to run ffprobe and get the duration info.
    * Shortcut would be to limit file size using **spring.http.multipart.max-request-size=128MB**
   * Break out the FileUpload Steam to somehow write to this new Stream opened by ffprobe
   * Figure out how to  configure ffprobe to read from a STDIN File Descripter, and get Java to wrote to it.
  - [ ] **Add swagger Documentation, and JavaDoc**
 - [ ] **Update Security**
    * Currrently uses Basic Authentication.  Modify to use form based using JWT tokens
     * Currently does not encrypt password. Add Login Form and use Use *BCryptPasswordEncoder()*.
     * Update code in *WebSecurityConfig* to update  *config(HttpSecurity)...* and *CorsFilter.java*
     * Test and ensure CORS, CSRF working and locked down for Rest Clients.  
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
  
  ## complete list of properties file changes.
``` sh
spring.datasource.url=jdbc:mysql://localhost:8889/my_db
spring.datasource.username=un
spring.datasource.password=pw
api.config.uploaddir=/upload
api.config.ffprobe.bin=/usr/bin/ffprobe
api.config.ffmpeg.bin=/usr/bin/ffmpeg
api.config.python.bin=/usr/bin/python

#Image Magick
api.config.mogrify=/usr/bin/mogrify
api.config.identify=/usr/bin/identify
api.config.montage=/usr/bin/montage

#Logging
logging.file=logs/spring-boot-logging.log
spring.output.ansi.enabled=always

#
# Support for TRANSCODING_PIPELINE file processing
# -keep it configuration based.
#
transcoding.assets.dest.dir=/var/www/html/XX/ASSETS/
transcoding.thumbs.processing.dir=thumbs/
transcoding.upload.dir=upload-dir/
transcoding.pipeline.processing.list=_sprite.jpg,_thumbs.json
transcoding.pipeline.asset.list=_sprite.jpg,_thumbs.json,.mp4


# HLS Processing. prototype. **REFACTOR ASAP** after you try .mpd and see pattern.
transcoding.assets.dest.dir.hls=/var/www/html/XX/ASSETS/HLS/
transcoding.hls.processing.dir=hls/

transcoding.output.base.url=https://myserver.com/XX/ASSETS/
 ```   
 


