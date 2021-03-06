// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.classes.Utils;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that posts and retrieves comments from Datastore.
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  /**
   * Gets the comments, in JSON format, taken from user input in the form from the HTML,
   * which are stored in the messages variable.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create a query and prepare it with the data stored in Datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery preparedQuery = datastore.prepare(query);

    // Set maximum number of comments to be included in the response.
    // Gets set to 0 when input is invalid.
    String numOfCommentsString = request.getParameter("comment-limit");
    int numOfComments = 0;
    try {
      numOfComments = Integer.parseInt(numOfCommentsString);
      if (numOfComments < 0) {
        throw new NumberFormatException("Number not valid (cannot be negative): " 
            + numOfCommentsString);
      }
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numOfCommentsString);
      numOfComments = 0;
    }
    List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withLimit(numOfComments));

    // Add all queried comments from Datastore
    // to a List of type Comment.
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results) {
      long id = entity.getKey().getId();
      String nickname = (String) entity.getProperty("nickname");
      String content = (String) entity.getProperty("content");
      String imageUrl = (String) entity.getProperty("imageUrl");
      long timestamp = (long) entity.getProperty("timestamp");

      Comment comment = new Comment(id, nickname, content, imageUrl, timestamp);
      comments.add(comment);
    }
    
    // Convert the comments List to JSON format.
    Gson gson = new Gson();

    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(comments));
  }

  /**
   * Posts a comment retrieved from the form input adding it to the messages variable.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    
    // Get the nickname, comment content, comment image and current time to add it to Datastore.
    String nickname = Utils.getUserNickname(userService.getCurrentUser().getUserId());
    String content = request.getParameter("text-input");
    String imageUrl = getUploadedFileUrl(request, "comment-image");
    long timestamp = System.currentTimeMillis();

    // Create an Entity that holds the comment, the image
    // and the time it was created and store it in Datastore.
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("nickname", nickname);
    commentEntity.setProperty("content", content);
    commentEntity.setProperty("imageUrl", imageUrl);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  /**
   * Returns a URL that points to the uploaded file, or null if the user didn't upload a file.
   */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL.
    // This is for dev servers (servers run locally).
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // The form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL.
    // This is for live servers (when the App Engine has been deployed).
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's devserver, we must use the relative
    // path to the image, rather than the path returned by imagesService which contains a host.
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch(MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }
}
