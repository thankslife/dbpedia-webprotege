package edu.stanford.bmir.protege.web.server;

import edu.stanford.bmir.protege.web.client.rpc.MediawikiService;
import edu.stanford.bmir.protege.web.client.rpc.data.LoginChallengeData;
import edu.stanford.bmir.protege.web.client.rpc.data.UserData;
import edu.stanford.bmir.protege.web.client.ui.login.constants.AuthenticationConstants;
import edu.stanford.bmir.protege.web.client.ui.openid.OpenIdUtil;
import edu.stanford.bmir.protege.web.client.ui.openid.constants.OpenIdConstants;
import edu.stanford.bmir.protege.web.server.logging.DefaultLogger;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIMetaProjectStore;
import edu.stanford.bmir.protege.web.shared.user.UnrecognizedUserNameException;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import edu.stanford.bmir.protege.web.shared.user.UserNameAlreadyExistsException;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.User;
import edu.stanford.smi.protege.server.metaproject.impl.UserImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.sql.*;
import java.util.Random;

/**
 * Created by peterr on 12.09.14.
 */
public class MediawikiServiceImpl extends AdminServiceImpl implements MediawikiService {

    private static final long serialVersionUID = 7616699639338297327L;

    private WebProtegeLogger logger = new DefaultLogger(MediawikiService.class);

    public boolean checkIfUserExists(String userName) {
        User user = MetaProjectManager.getManager().getUser(userName);
        return user != null;
    }

    public void setSessionValues(String userName, String cookie_prefix, String session_id, String edit_token) {
        MetaProjectManager metaProjectManager = MetaProjectManager.getManager();
        User user = metaProjectManager.getUser(userName);
        user.removePropertyValue(userName + "_session_prefix", user.getPropertyValue("session_prefix"));
        user.removePropertyValue(userName + "_session_cookie", user.getPropertyValue("session_cookie"));
        user.removePropertyValue(userName + "_token", user.getPropertyValue("token"));
        user.setPropertyValue(userName + "_token", edit_token);
        user.setPropertyValue(userName + "_session_prefix", cookie_prefix);
        user.setPropertyValue(userName + "_session_cookie", session_id);
        OWLAPIMetaProjectStore.getStore().saveMetaProjectNow(metaProjectManager);
    }

//    public UserData registerUserViaEncrption(String name, String hashedPassword, String emailId) throws UserNameAlreadyExistsException, UserNameAlreadyExistsException {
//        UserData userData = registerUserViaEncrption(name, hashedPassword, emailId);
//        MetaProjectManager metaProjectManager = MetaProjectManager.getManager();
//        OWLAPIMetaProjectStore.getStore().saveMetaProjectNow(metaProjectManager);
//
//        return userData;
//    }

//    public UserId authenticateToLogin(String userNameOrEmail, String response) {
//        HttpServletRequest request = this.getThreadLocalRequest();
//        HttpSession session = request.getSession();
//
//        final String challenge = (String) session.getAttribute(AuthenticationConstants.LOGIN_CHALLENGE);
//        session.setAttribute(AuthenticationConstants.LOGIN_CHALLENGE, null);
//
//        User user = MetaProjectManager.getManager().getUser(userNameOrEmail);
//        if (user == null) { //user not in metaproject
//            return UserId.getGuest();
//        }
//        AuthenticationUtil authenticatinUtil = new AuthenticationUtil();
//        boolean isverified = authenticatinUtil.verifyChallengedHash(user.getDigestedPassword(), response, challenge);
//        if (isverified) {
//            UserId userId = UserId.getUserId(user.getName());
//            SessionConstants.setAttribute(SessionConstants.USER_ID, userId, session);
//            return userId;
//        }
//        else {
//            return UserId.getGuest();
//        }
//
//    }

//    public UserId getCurrentUserInSession() {
//        HttpServletRequest request = getThreadLocalRequest();
//        final HttpSession session = request.getSession();
//        return SessionConstants.getUserId(session);
//    }
//
//    public void logout() {
//        HttpServletRequest request = getThreadLocalRequest();
//        final HttpSession session = request.getSession();
//        SessionConstants.removeAttribute(SessionConstants.USER_ID, session);
//    }
//
//    public void changePassword(String userName, String password) {
//        MetaProjectManager.getManager().changePassword(userName, password);
//    }
//
//    public String getUserEmail(String userName) {
//        return MetaProjectManager.getManager().getUserEmail(userName);
//    }
//
//    public void setUserEmail(String userName, String email) {
//        MetaProjectManager.getManager().setUserEmail(userName, email);
//    }
//
//    public PermissionsSet getAllowedOperations(String project, String user) {
//        Collection<Operation> ops = MetaProjectManager.getManager().getAllowedOperations(project, user);
//        return toPermissionSet(ops);
//    }
//
//    public PermissionsSet getAllowedServerOperations(String userName) {
//        Collection<Operation> ops = MetaProjectManager.getManager().getAllowedServerOperations(userName);
//        return toPermissionSet(ops);
//    }
//
//    private PermissionsSet toPermissionSet(Collection<Operation> ops) {
//        PermissionsSet.Builder builder = PermissionsSet.builder();
//        for (Operation op : ops) {
//            builder.addPermission(Permission.getPermission(op.getName()));
//        }
//        return builder.build();
//    }
//
//    public void sendPasswordReminder(String userName) throws UnrecognizedUserNameException {
//        String email = MetaProjectManager.getManager().getUserEmail(userName);
//        if (email == null) {
//            throw new UnrecognizedUserNameException("User " + userName + " does not have an email configured.");
//        }
//        changePassword(userName, EmailConstants.RESET_PASSWORD);
//        App.get().getMailManager().sendMail(email, EmailConstants.FORGOT_PASSWORD_SUBJECT, EmailConstants.FORGOT_PASSWORD_EMAIL_BODY);
//    }
//
//    public LoginChallengeData getUserSaltAndChallenge(String userNameOrEmail) {
//        User user = MetaProjectManager.getManager().getUser(userNameOrEmail);
//        if (user == null) {
//            return null;
//        }
//        String userSalt = user.getSalt();
//        if (userSalt == null) {
//            return null;
//        }
//        String encodedChallenge = generateSalt();
//        HttpServletRequest request = this.getThreadLocalRequest();
//        HttpSession session = request.getSession();
//        session.setAttribute(AuthenticationConstants.LOGIN_CHALLENGE, encodedChallenge);
//        return new LoginChallengeData(userSalt, encodedChallenge);
//    }
//
//    public boolean allowsCreateUsers() {
//        return MetaProjectManager.getManager().allowsCreateUser();
//    }
//
//    public UserId authenticateToLogin(String userNameOrEmail, String response) {
//        HttpServletRequest request = this.getThreadLocalRequest();
//        HttpSession session = request.getSession();
//
//        final String challenge = (String) session.getAttribute(AuthenticationConstants.LOGIN_CHALLENGE);
//        session.setAttribute(AuthenticationConstants.LOGIN_CHALLENGE, null);
//
//        User user = MetaProjectManager.getManager().getUser(userNameOrEmail);
//        if (user == null) { //user not in metaproject
//            return UserId.getGuest();
//        }
//        AuthenticationUtil authenticatinUtil = new AuthenticationUtil();
//        boolean isverified = authenticatinUtil.verifyChallengedHash(user.getDigestedPassword(), response, challenge);
//        if (isverified) {
//            UserId userId = UserId.getUserId(user.getName());
//            SessionConstants.setAttribute(SessionConstants.USER_ID, userId, session);
//            return userId;
//        }
//        else {
//            return UserId.getGuest();
//        }
//
//    }
//
//    private static String encodeBytes(byte[] bytes) {
//        int stringLength = 2 * bytes.length;
//        BigInteger bi = new BigInteger(1, bytes);
//        String encoded = bi.toString(16);
//        while (encoded.length() < stringLength) {
//            encoded = "0" + encoded;
//        }
//        return encoded;
//    }
//
//    private String generateSalt() {
//        byte[] salt = new byte[8];
//        Random random = new Random();
//        random.nextBytes(salt);
//        String encodedSalt = encodeBytes(salt);
//        return encodedSalt;
//    }
//
//    public String checkUserLoggedInMethod() {
//        String loginMethod = null;
//        HttpServletRequest request = this.getThreadLocalRequest();
//        HttpSession sess = request.getSession();
//        loginMethod = (String) sess.getAttribute(AuthenticationConstants.LOGIN_METHOD);
//        return loginMethod;
//    }
//
//    public void clearPreviousLoginAuthenticationData() {
//        HttpServletRequest request = this.getThreadLocalRequest();
//        HttpSession session = request.getSession();
//        session.setAttribute(AuthenticationConstants.LOGIN_METHOD, null);
//        SessionConstants.removeAttribute(SessionConstants.USER_ID, session);
//        SessionConstants.removeAttribute(SessionConstants.OPEN_ID_ACCOUNT, session);
//    }
//
//    public boolean changePasswordEncrypted(String userName, String encryptedPassword, String salt) {
//        User user = MetaProjectManager.getManager().getMetaProject().getUser(userName);
//        if (user == null) {
//            return false;
//        }
//        user.setDigestedPassword(encryptedPassword, salt);
//        return true;
//    }
//
//    public String getNewSalt() {
//        Random random = new Random();
//        byte[] salt = new byte[8];
//        random.nextBytes(salt);
//        String newSalt = encodeBytes(salt);
//        HttpServletRequest request = this.getThreadLocalRequest();
//        HttpSession session = request.getSession();
//        session.setAttribute(AuthenticationConstants.NEW_SALT, newSalt);
//        return newSalt;
//    }
//
//    //used only for https
//    public UserData registerUser(String userName, String password, String email) {
//        MetaProjectManager mpm = MetaProjectManager.getManager();
//        UserData userData = mpm.registerUser(userName, email, password);
//        OWLAPIMetaProjectStore.getStore().saveMetaProject(mpm);
//        return userData;
//    }

    public UserData registerUserViaEncrption(String name, String hashedPassword, String emailId) throws UserNameAlreadyExistsException, UserNameAlreadyExistsException {
        HttpServletRequest request = this.getThreadLocalRequest();
        HttpSession session = request.getSession();
        String salt = (String) session.getAttribute(AuthenticationConstants.NEW_SALT);
        String emptyPassword = "";

        MetaProjectManager metaProjectManager = MetaProjectManager.getManager();
        UserData userData = metaProjectManager.registerUser(name, emailId, emptyPassword);

        User user = metaProjectManager.getMetaProject().getUser(name);
        user.setDigestedPassword(hashedPassword, salt);
        user.setEmail(emailId);

        OWLAPIMetaProjectStore.getStore().saveMetaProject(metaProjectManager);
        OWLAPIMetaProjectStore.getStore().saveMetaProjectNow(metaProjectManager);

        return userData;
    }
}
