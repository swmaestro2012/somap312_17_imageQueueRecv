package so.tree.imageQueue.Recv;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3UploadModule {
	
	private String accessKey;
	private String secretKey;
	private String bucketName;
	private String key;
	private boolean makePublic;

	public S3UploadModule() {
		super();
	}

	public S3UploadModule(String accessKey, String secretKey, String bucketName, String key, boolean makePublic) {
		super();
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.bucketName = bucketName;
		this.key = key;
		this.makePublic = makePublic;
	}

	public void upload(){
		AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
		
		try {

			System.out.print("Uploading " + key + " to S3...");
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType("image/png");
			PutObjectRequest por = new PutObjectRequest(bucketName, key, new File(Settings.IMAGE_PATH + key));
			por.setMetadata(objectMetadata);
			s3.putObject(por);
			System.out.print("Uploaded!... ");
			
			if(makePublic){
				AccessControlList acl = s3.getBucketAcl(bucketName);
				acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
				s3.setObjectAcl(bucketName, key, acl);
				System.out.print("Grantee to Everyone!\n");
			}
			
			
			
		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with S3, "
							+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void updateUserLookEntity() throws ClientProtocolException, IOException{
		HttpClient client = new DefaultHttpClient(); 
		HttpPut httpPut = new HttpPut(Settings.PLAY_SERVER + key);
		client.execute(httpPut);
	}
	
	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public boolean isMakePublic() {
		return makePublic;
	}

	public void setMakePublic(boolean makePublic) {
		this.makePublic = makePublic;
	}

}
