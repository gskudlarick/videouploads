package com.ges.video.domain.video;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "media_id")
    private Integer mediaId;
    
    //@Embedded
    //private Dummy dummy;
    
    private Format format;
    private VideoUrls videourls;

    private String name;
    private String description;
    
    @Column(name = "meta_data")
    private String metaData;
    
    
    @Column(name = "author")
    private String author;
    
    @Column(name = "author_ip_address")
    private String authorIpAddress;
    
    @Column(name = "author_location")
    private String authorLocation;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "upload_date")
    private java.util.Date uploadDate;
    
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;
    
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
    
    @Transient
    MultipartFile file;
    
    
    
    
    /****************** 
     * SETTERS GETTERS
     ******************/

	public Integer getMediaId() {
		return mediaId;
	}

	public void setMediaId(Integer mediaId) {
		this.mediaId = mediaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorIpAddress() {
		return authorIpAddress;
	}

	public void setAuthorIpAddress(String authorIpAddress) {
		this.authorIpAddress = authorIpAddress;
	}

	public String getAuthorLocation() {
		return authorLocation;
	}

	public void setAuthorLocation(String authorLocation) {
		this.authorLocation = authorLocation;
	}

	public java.util.Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(java.util.Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public VideoUrls getVideourls() {
		return videourls;
	}

	public void setVideourls(VideoUrls videourls) {
		this.videourls = videourls;
	}


	
	
    


    
    
}


