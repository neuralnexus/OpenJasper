/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.rest;

import com.jaspersoft.jasperserver.remote.services.LegacyRunReportService;
import com.jaspersoft.jasperserver.ws.authority.WSRoleSearchCriteria;
import com.jaspersoft.jasperserver.ws.authority.WSUserSearchCriteria;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author gtoffoli
 * @version $Id: RESTUtils.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class RESTUtils {

    private final static Log log = LogFactory.getLog(RESTUtils.class);

    public static final String REQUEST_PARAMENTER_RD = "ResourceDescriptor";
    public static final String REQUEST_PARAMENTER_MOVE_TO = "moveTo";
    public static final String REQUEST_PARAMENTER_COPY_TO = "copyTo";
    public static final String SWITCH_PARAM_GET_LOCAL_RESOURCE = "GET_LOCAL_RESOURCE"; //indicates that the first call was made to retrieve a local resource
    public static final String REQUEST_PARAMENTER_ROLES = "roles";
    public static final String REQUEST_PARAMENTER_USERS = "users";
    public static final String REQUEST_PARAMENTER_SEPARATOR = ",";
    public static final String REQUEST_PARAMENTER_ATTRIBUTES = "attributeKeys";
    public static final String REQUEST_PARAMENTER_LIST_SUB_ORGS = "listSubOrgs";




    public static final String PARAMETER_ATTRIBUTE_SEPARATOR = ","; //separates between the values that require to define parameter
    public static final String PARAMETER_SEPARATOR = ";"; // separates between parameters
    // fieldName1, theFieldName1, filedType1;
    // fieldName2, theFieldName2, filedType2;


    private static HashMap<String, Marshaller> marshallers = new HashMap<String, Marshaller>();
    public static final String FILE_DATA = "fileData";


    /*
     * Convenient method to check if the current request is of type multipart.
     *
     * @param request
     * @return
     */
     public static boolean isMultipartContent(HttpServletRequest request) {
        if ( !(RESTAbstractService.HTTP_PUT.equals(request.getMethod().toLowerCase()) || RESTAbstractService.HTTP_POST.equals(request.getMethod().toLowerCase())) ) {
            return false;
        }
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        if (contentType.toLowerCase().startsWith("multipart/")) {
            return true;
        }
        return false;
    }

     
   /**
    * Extract the attachments from the request and put the in the list of
    * input attachments of the service.
    *
    * The method returns the currenst HttpServletRequest if the message is not
    * multipart, otherwise it returns a MultipartHttpServletRequest
    *
    *
    * @param service
    * @param hRequest
    */
    public static HttpServletRequest extractAttachments(LegacyRunReportService service, HttpServletRequest hRequest)
    {
          //Check whether we're dealing with a multipart request
          MultipartResolver resolver= new CommonsMultipartResolver();

          // handles the PUT multipart requests
          if(isMultipartContent(hRequest) && hRequest.getContentLength() != -1)
          {
              MultipartHttpServletRequest mreq = resolver.resolveMultipart(hRequest);
              if(mreq!=null && mreq.getFileMap().size()!=0)
              {
                      Iterator iterator=mreq.getFileNames();
                      String fieldName=null;
                      while(iterator.hasNext()){
                              fieldName=(String)iterator.next();
                              MultipartFile file=mreq.getFile(fieldName);
                              if(file!=null){
                                  DataSource ds = new MultipartFileDataSource(file);
                                  service.getInputAttachments().put(fieldName, ds);
                              }
                      }
                  if (log.isDebugEnabled()) {
                        log.debug(service.getInputAttachments().size()+" attachments were extracted from the PUT");
                  }
                  return mreq;
              }
              // handles the POST multipart requests
              else {
                    if (hRequest instanceof DefaultMultipartHttpServletRequest){
                        DefaultMultipartHttpServletRequest dmServletRequest = (DefaultMultipartHttpServletRequest)hRequest;

                        Iterator iterator= ((DefaultMultipartHttpServletRequest) hRequest).getFileNames();
                        String fieldName=null;
                        while(iterator.hasNext()){
                            fieldName=(String)iterator.next();
                            MultipartFile file=dmServletRequest.getFile(fieldName);
                            if(file!=null){
                                  DataSource ds = new MultipartFileDataSource(file);
                                  service.getInputAttachments().put(fieldName, ds);
                            }
                        }
                    }
              }
            }
          if (log.isDebugEnabled()) {
                log.debug(service.getInputAttachments().size()+" attachments were extracted from the POST");
          }
          return hRequest;
        
    }


    /**
     * Return the service name from the request.
     * The service name is the first item in the path, i.e.
     * /serviceName/somethingelse
     * 
     *
     *
     * @param path
     * @return The service name, or an empty string if the service name is empty
     */
    public static String extractServiceName(String path)
    {
        if (path == null || path.length() == 0) return "";
        if (path.startsWith("/"))
        {
            path = path.substring(1);
        }
        int startIndex = path.indexOf("/");
        if (startIndex > 0)
        {
            path = path.substring(0, startIndex);
        }

        return path;
    }

    /**
     * Return the repository uri from a pathInfo
     * The repository uri is the part of the url after the
     * service name
     *
     * /serviceName/this/it/the/uri
     *
     * The method looks for the first occurence of a / after
     * the service name.
     *
     * Any final / is removed (until the uri represent the root path).
     *
     * @param path
     * @return a path without the last / or just the root path (/) if a valid path is not found.
     */
    public static String extractRepositoryUri(String path)
    {
        if (path == null) return "/";

        if (path.startsWith("/"))
        {
            path = path.substring(1);
        }
        int startIndex = path.indexOf("/");
        if (startIndex > 0)
        {
            path = path.substring(startIndex);
        }

        while (path.length() > 1 && path.endsWith("/"))
        {
            path = path.substring(0,path.length()-1);
        }

        return path;
    }

    // removes \ and / from the string
    public static String getURIName(String uri){
        return (uri.replace("\\", "")).replace("//", "");
    }


   /**
    * Look for parameters provided by the client.
    *
    * @param req  HttpServletRequest
    * @return Map&lt;String,Object&gt;
    */
    public static Map<String,Object> extractParameters(HttpServletRequest req)
    {
        Map<String,Object> parameters = new HashMap<String,Object>();
        Enumeration penum = req.getParameterNames();
        while (penum.hasMoreElements())
        {
            String pname = (String) penum.nextElement();
            if (pname.startsWith("P_"))
            {
                parameters.put(pname.substring(2), req.getParameter(pname));
            }
            else if(pname.startsWith("PL_"))
            {
                parameters.put(pname.substring(3), Arrays.asList(req.getParameterValues(pname)));
            }
        }
        return parameters;
    }

    /**
     * Set the status of the response to the errorCode and send the message to the client
     *
     * @param errorCode the errorCode (see HttpServletResponse for common HTTP status codes)
     * @param response the HttpServletResponse
     * @param body if null, an empty string is sent.
     */
    public static void setStatusAndBody(int errorCode, HttpServletResponse response, String body)
    {
        response.setStatus( errorCode );
        // we can put an error in the output too (so avoid the webserver to display unwanted pages...)
        try {
            PrintWriter pw = response.getWriter();
            pw.print((body == null) ? "" : body);
        } catch (Exception ioEx)
        {
            // do nothing. If we have an I/O error, we just avoid to specify unuseful errors here.
            log.error("Error sending output a file",ioEx);
        }
    }


    public static void sendFile(DataSource ds, HttpServletResponse response)
    {
        response.setContentType( ds.getContentType() );

        if (ds.getName() != null && ds.getName().length() > 0)
        {
            response.addHeader("Content-Disposition", "attachment; filename=" + ds.getName());
        }

        OutputStream outputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
             outputStream = response.getOutputStream();

             bufferedInputStream = new BufferedInputStream(ds.getInputStream());
             int readBytes = 0;

             while ((readBytes = bufferedInputStream.read()) != -1)
             {
                outputStream.write(readBytes);
             }

             if (log.isDebugEnabled()) {
                log.debug("finished sending bytes");
             }


        } catch (IOException ex) {
            log.error("Error serving a file: " + ex.getMessage(), ex);
        } finally {
          if (outputStream != null) try { outputStream.close(); } catch (Exception ex) {}
          if (bufferedInputStream != null) try {  bufferedInputStream.close(); } catch (Exception ex) {}
        }
    }

    public static String getDetinationUri(HttpServletRequest req){ // for action that specify the destination of the action in the request
        if ( req.getParameterMap().containsKey(REQUEST_PARAMENTER_MOVE_TO) )
            return req.getParameter(REQUEST_PARAMENTER_MOVE_TO);

        if ( req.getParameterMap().containsKey(REQUEST_PARAMENTER_COPY_TO) )
            return req.getParameter(REQUEST_PARAMENTER_COPY_TO);

        return null;
    }

    public static Marshaller getMarshaller(boolean isFragment, Class... docClass) throws JAXBException {
            JAXBContext context = JAXBContext.newInstance(docClass);
            Marshaller m = context.createMarshaller();
            m.setProperty(javax.xml.bind.Marshaller.JAXB_FRAGMENT,isFragment);
            return m;
    }

    public static <T> T unmarshal( Class<T> docClass, InputStream inputStream ) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance( docClass );
        Unmarshaller u = jc.createUnmarshaller();
        return (T) u.unmarshal(inputStream);
    }

    public static Marshaller getMarshaller(Class... docClass) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(docClass);
        Marshaller m = context.createMarshaller();
        m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(javax.xml.bind.Marshaller.JAXB_FRAGMENT,Boolean.TRUE);
        return m;
    }

    public static Object unmarshal( InputStream inputStream, Class... docClass ) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance( docClass );
        Unmarshaller u = jc.createUnmarshaller();
        return u.unmarshal(inputStream);
    }

    // extracts the last part of the url with out any editions: /rest/user/chaim => chaim
    public static String extractResourceName(String serviceName, String path) {
        if (serviceName==null){
            serviceName = "/"+serviceName+"/";
            return path.substring(path.indexOf(serviceName)+serviceName.length(), path.length());
        }
        else {
            return extractResourceName(path);
        }
    }

    // extracts the last part of the url with out any editions: /rest/user/chaim => chaim
    public static String extractResourceName(String pathInfo) {
        return pathInfo.substring(pathInfo.lastIndexOf("/")+"/".length());
    }

    public static boolean isLocalResource(String uri) {
        return getParentFolder(uri).endsWith("_files");
    }


    //return the parent uri
    private static String getParentFolder(String uri) {
        int parentFolderNameEnd = uri.lastIndexOf("/");

        if (parentFolderNameEnd==-1 || parentFolderNameEnd==0) //
            return "";

        uri = uri.substring(0, parentFolderNameEnd);
        int parentFolderNameStart = uri.lastIndexOf("/");
        return  uri.substring(parentFolderNameStart);
    }

    public static List<String> stringToList(String roles, String delimiter) {
        List<String> lst = new LinkedList<String>();
        StringTokenizer sTok = new StringTokenizer(roles, delimiter);

        while (sTok.hasMoreTokens()){
            lst.add(sTok.nextToken().trim());
        }

        return lst;
    }

    @XmlRootElement(name="entityResource")
    public static class JAXBList<T> {
        @XmlElement(name="Item")
        List<T> list=new ArrayList<T>();
        public JAXBList (){}
        public JAXBList (List<T> lst){
            list.addAll(lst);
        }

        public JAXBList (T[] arr){
            list = Arrays.asList(arr);
        }

       public void add(T element){
           list.add(element);
       }

       public int size(){
           return list.size();
       }

       public T get(int index){
           return list.get(index);
       }
     }

    public static WSUserSearchCriteria getWSUserSearchCriteria(String searchCriteria) {
        WSUserSearchCriteria c = new WSUserSearchCriteria();
        String userName, tenantId;
        int tenantStartIndex = searchCriteria.indexOf("|");
        if (tenantStartIndex==-1){
            c.setName(searchCriteria);
            c.setTenantId(null);
            if (log.isDebugEnabled()) {
                log.debug("user search criteria was created. userName: "+ searchCriteria);
            }
        }
        else {
            userName = searchCriteria.substring(0, tenantStartIndex);
            tenantId = searchCriteria.substring(tenantStartIndex+1, searchCriteria.length());
            if (log.isDebugEnabled()) {
                log.debug("user search criteria was created. userName: "+ userName +" tenant:"+tenantId);
            }

            c.setName(userName);
            c.setTenantId(tenantId);
        }

        return c;
    }

    public static WSRoleSearchCriteria getWSRoleSearchCriteria(String searchCriteria)
    {
        WSRoleSearchCriteria c = new WSRoleSearchCriteria();
        String userName, tenantId;
        int tenantStartIndex = searchCriteria.indexOf("|");

        if (tenantStartIndex==-1)
        {
            c.setRoleName(searchCriteria);
            c.setTenantId(null);
            if (log.isDebugEnabled()) {
                log.debug("role search criteria was created. roleName: "+ searchCriteria+" tenant: null");
            }
        }
        else {
            userName = searchCriteria.substring(0, tenantStartIndex);
            tenantId = searchCriteria.substring(tenantStartIndex+1, searchCriteria.length());
            if (log.isDebugEnabled()) {
                log.debug("role search criteria was created. roleName: "+ userName +" tenant:"+tenantId);
            }

            c.setRoleName(userName);
            c.setTenantId(tenantId);
        }

        return c;
    }

    public static String removeSpecialSpaces(String str){
        str = str.replaceAll("\\\n", "");
        str = str.replaceAll("\\\t", "");
        return str;
    }

    public static UserDetails getCurrentlyLoggedUser(){
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails)authenticationToken.getPrincipal();
    }
}
