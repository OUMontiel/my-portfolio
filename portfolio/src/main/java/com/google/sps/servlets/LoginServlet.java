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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that creates login or logout URL and sends it as response. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    Boolean loggedIn = false;
    String logUrl = ""; // URL used for either login or logout.
    String userEmail = "";
    String redirectUrl = "/"; // Both login and logout redirect to the same URL

    if (userService.isUserLoggedIn()) {
      // Create a new user with logout URL and email.
      loggedIn = true;
      logUrl = userService.createLogoutURL(redirectUrl);
      userEmail = userService.getCurrentUser().getEmail();
    } else {
      // Create a new user with login URL and no email.
      logUrl = userService.createLoginURL(redirectUrl);
    }

    // Create new user.
    User user = new User(loggedIn, logUrl, userEmail);

    // Convert the user to JSON format and return it as response.
    Gson gson = new Gson();
    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(user));
  }
}
