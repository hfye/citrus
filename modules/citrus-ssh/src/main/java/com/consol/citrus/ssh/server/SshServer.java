/*
 * Copyright 2006-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.ssh.server;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.server.AbstractServer;
import com.consol.citrus.ssh.SshCommand;
import org.apache.sshd.common.KeyPairProvider;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.keyprovider.ResourceKeyPairProvider;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * SSH Server implemented with Apache SSHD (http://mina.apache.org/sshd/).
 *
 * It uses the same semantic as the Jetty Servers for HTTP and WS mocks and translates
 * an incoming request into a message, for which a reply message gets translates to
 * the SSH return value.
 *
 * The incoming message generated has the following format:
 *
 * <ssh-request>
 *   <command>cat -</command>
 *   <stdin>This is the standard input sent</stdin>
 * </ssh-request>
 *
 * The reply message to be generated by a handler should have the following format
 *
 * <ssh-response>
 *   <exit>0</exit>
 *   <stdout>This is the standard input sent</stdout>
 *   <stderr>warning: no tty</stderr>
 * </ssh-response>
 *
 * @author Roland Huss
 * @since 04.09.12
 */
public class SshServer extends AbstractServer {

    /** Port to listen to **/
    private int port = 22;

    /** User allowed to connect **/
    private String user;

    /** User's password or ... **/
    private String password;

    /** ... path to its public key **/
    /** Use this to convert to PEM: ssh-keygen -f key.pub -e -m pem **/
    private String allowedKeyPath;

    /** Path to our own host keys. If not provided, a default is used. The format of this **/
    /** file should be PEM, a serialized {@link java.security.KeyPair}. **/
    private String hostKeyPath;

    /** SSH server used **/
    private org.apache.sshd.SshServer sshd;

    @Override
    protected void startup() {
        if (!StringUtils.hasText(user)) {
            throw new CitrusRuntimeException("No 'user' provided (mandatory for authentication)");
        }
        sshd = org.apache.sshd.SshServer.setUpDefaultServer();
        sshd.setPort(port);
        KeyPairProvider prov =
                hostKeyPath != null ?
                        new FileKeyPairProvider(new String[] {hostKeyPath}) :
                        new ResourceKeyPairProvider(new String[] { "com/consol/citrus/ssh/citrus.pem" });
        sshd.setKeyPairProvider(prov);

        // Authentication
        boolean authFound = false;
        if (password != null) {
            sshd.setPasswordAuthenticator(new SimplePasswordAuthenticator(user, password));
            authFound = true;
        }

        if (allowedKeyPath != null) {
            sshd.setPublickeyAuthenticator(new SinglePublicKeyAuthenticator(user, allowedKeyPath));
            authFound = true;
        }

        if (!authFound) {
            throw new CitrusRuntimeException("Neither 'password' nor 'allowed-key-path' is set. Please provide at least one");
        }

        // Setup endpoint adapter
        sshd.setCommandFactory(new CommandFactory() {
            public Command createCommand(String command) {
                return new SshCommand(command, getEndpointAdapter());
            }
        });

        try {
            sshd.start();
        } catch (IOException e) {
            throw new CitrusRuntimeException("Cannot start SSHD: " + e,e);
        }
    }

    @Override
    protected void shutdown() {
        try {
            sshd.stop();
        } catch (InterruptedException e) {
            throw new CitrusRuntimeException("Cannot stop SSHD: " + e,e);
        }
    }

    /**
     * Gets the server port.
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port.
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets the username.
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user.
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets the user password.
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the allowed key path.
     * @return
     */
    public String getAllowedKeyPath() {
        return allowedKeyPath;
    }

    /**
     * Sets the allowedKeyPath.
     * @param allowedKeyPath the allowedKeyPath to set
     */
    public void setAllowedKeyPath(String allowedKeyPath) {
        this.allowedKeyPath = allowedKeyPath;
    }

    /**
     * Gets the host key path.
     * @return
     */
    public String getHostKeyPath() {
        return hostKeyPath;
    }

    /**
     * Sets the hostKeyPath.
     * @param hostKeyPath the hostKeyPath to set
     */
    public void setHostKeyPath(String hostKeyPath) {
        this.hostKeyPath = hostKeyPath;
    }

}
