/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.iie.googleplus.OAuth;

import com.google.api.services.plus.Plus;
import com.iie.googleplus.Crawler.Authenticate;

/**
 * Main class for the Google+ API command line sample.
 * Demonstrates how to make an authenticated API call using OAuth 2 helper classes.
 */
public class GPlusAPICrawler {
  
  private static Plus plus;
  
  public static int count = 1; 
  public static int appNum = 1;
 

  public static void main(String[] args) throws Exception {
	  
	  // 一次做完全部认证
	  for (int i = appNum; i <= 20; i++) {
    	  plus = Authenticate.AuthorizedGenerator(i);  
	  }
	  
	  System.out.println("OAuth2.0 全部认证完毕");
	  
	  boolean needsProxy = true;
	  if (needsProxy) {
		    System.getProperties().put("proxySet", "true");
		    System.getProperties().put("proxyHost", "192.168.120.180");
		    System.getProperties().put("proxyPort", "8087");
	  } else {
		    System.getProperties().put("proxySet", "false");
		    System.getProperties().put("proxyHost", "");
		    System.getProperties().put("proxyPort", "");
	  }
  
  }
}
