/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.buildomatic.crypto;

import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.logging.log4j.core.util.StringBuilderWriter;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import static com.jaspersoft.jasperserver.crypto.KeystoreManager.KS_NAME;
import static com.jaspersoft.jasperserver.crypto.KeystoreManager.KS_PROP_NAME;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static org.apache.tools.ant.Project.*;

/**
 * This class is invoked by ant during the build.  It, in turn, calls KeystoreManager to generate
 * a centralized keystore and secret keys.
 * <p>
 * User: dlitvak, schubar
 * Date: 7/16/13
 */
public class KeystoreTask extends Task {
    /**
     * keystore file location
     */
    private String ks = ".";

    /**
     * keystore property file location
     */
    private String ksp = ".";

    private String propsFile = null;
    private String confirmArg = null;
    private String confirmMessage = null;
    private String warningMessage = null;

    private org.apache.tools.ant.types.Path classpath;

    @Override
    public void execute() throws BuildException {
        if (propsFile == null) {
            throw new RuntimeException("Master properties file is not specified");
        }

        if (!exists(Paths.get(propsFile))) {
            throw new RuntimeException("Master properties file doesn't exist at specified path " + propsFile);
        }

        final Path ksPath = Paths.get(ks).toAbsolutePath().normalize();
        final String ksDir = ksPath.toString();
        final String ksPropDir = ksPath.toString();

        if (!Files.exists(Paths.get(ksDir, KS_PROP_NAME)) || !Files.exists(Paths.get(ksPropDir, KS_NAME))) {
            if (confirmMessage != null && !confirmMessage.isEmpty()) {
                final String confirmKey = this.confirmArg == null || this.confirmArg.isEmpty() ? "y" : this.confirmArg.trim();
                final String fmtMsg = confirmMessage + " \n";

                final String input;
                if (System.console() == null) { // On PFA and when running ./js-upgrade-newdb.sh System.console() is not available
                    System.out.print(String.format(fmtMsg, confirmKey));
                    Scanner scanner = new Scanner(System.in);

                    input = scanner.hasNextLine() ? scanner.nextLine() : "";
                } else {
                    input = System.console()
                            .readLine(fmtMsg, confirmKey);
                }

                if (input == null || !input.trim().equals(confirmKey)) {
                    throw new BuildException("Keystore creation was canceled.");
                }
            }

            if (warningMessage != null && !warningMessage.isEmpty()) {
                System.out.println(warningMessage);
            }
        }


        log(format("\tks=%s;\n\tksp=%s;\n", ksDir, ksPropDir), MSG_DEBUG);
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (classpath!= null){
            // This is needed because KeystoreManager is using Class.forName. Unfortunately by default classpath is not valid
            Method method;
            try {
                method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
                method.setAccessible(true);

                Arrays.stream( classpath.list()).forEach(path -> {
//                    System.out.println(path);
                    try {
                        URL url = new File(path).toURI().toURL();
                        method.invoke(contextClassLoader, new Object[]{url});
                    } catch (MalformedURLException | IllegalAccessException | InvocationTargetException e) {
                        throw new BuildException(format("Failed to add lib %s to the classpath", path), e, getLocation());
                    }
                });
            } catch (NoSuchMethodException e) {
                throw new BuildException("Failed to setup the class loader", e, getLocation());
            }
        }

        try {
            KeystoreManager.init(ksDir, ksPropDir, new File(propsFile));

            //log(Paths.get(ksPropDir, KeystoreManager.KS_PROP_NAME), MSG_DEBUG); //debug
        } catch (Exception e) {
            log(e, MSG_ERR);
            /*log(Paths.get(ksPropDir, KS_PROP_NAME), MSG_ERR);*/
            throw new BuildException("Keystore may have been tampered with.", e, getLocation());
        }
    }

    public org.apache.tools.ant.types.Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new org.apache.tools.ant.types.Path(getProject());
        }
        return this.classpath.createPath();
    }

    /**
     * For debugging only. Expensive
     * public void log(Path filePath, final int level) throws BuildException {
     * if(!Files.exists(filePath)) {
     * log(format("-- properties does't exist at %s --", filePath.toString()), level);
     * return;
     * }
     * <p>
     * String path = filePath.toString();
     * <p>
     * log(format("-- properties location = %s --", path), level);
     * <p>
     * try (Base64InputStream fis = new Base64InputStream(new FileInputStream(path))) {
     * BufferedInputStream in = new BufferedInputStream(fis);
     * byte[] buffer = new byte[8192];
     * <p>
     * for (int length = 0; (length = in.read(buffer)) != -1;) {
     * log(new String(buffer), level);
     * }
     * <p>
     * log("-- end of properties --", level);
     * } catch (IOException e) {
     * log("Failed to log master properties. ", e, MSG_ERR);
     * throw new BuildException("Keystore could not be initialized.", e, getLocation());
     * }
     * }
     */


    public String getKs() {
        return ks;
    }

    public void setKs(String directory) {
        this.ks = directory;
    }

    public String getKsp() {
        return ksp;
    }

    public void setKsp(String ksProp) {
        this.ksp = ksProp;
    }

    public String getPropsFile() {
        return propsFile;
    }

    public void setPropsFile(String propsFile) {
        this.propsFile = propsFile;
    }

    public void setConfirmMessage(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }

    public void setConfirmArg(String confirmArg) {
        this.confirmArg = confirmArg;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    /**
     * Sets the classpath for loading the configuration.
     *
     * @param classpath The classpath to set
     */
    public void setClasspath(org.apache.tools.ant.types.Path classpath) {
        createClasspath().add(classpath);
    }

}
