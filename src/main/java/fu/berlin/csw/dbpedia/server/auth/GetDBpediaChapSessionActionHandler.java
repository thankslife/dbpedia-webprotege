package fu.berlin.csw.dbpedia.server.auth;

import com.google.common.base.Optional;
import edu.stanford.bmir.protege.web.server.auth.ChapSessionManager;
import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.metaproject.AuthenticationManager;
import edu.stanford.bmir.protege.web.shared.auth.ChapSession;
import edu.stanford.bmir.protege.web.shared.auth.Salt;
import fu.berlin.csw.dbpedia.shared.auth.GetChapSessionAction;
import fu.berlin.csw.dbpedia.shared.auth.GetChapSessionResult;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14/02/15
 */
public class GetDBpediaChapSessionActionHandler implements ActionHandler<GetChapSessionAction, GetChapSessionResult> {

    private WebProtegeLogger logger;

    private ChapSessionManager chapSessionManager;

    private AuthenticationManager authenticationManager;

    @Inject
    public GetDBpediaChapSessionActionHandler(ChapSessionManager chapSessionManager,
                                              AuthenticationManager authenticationManager,
                                              WebProtegeLogger logger) {
        this.chapSessionManager = checkNotNull(chapSessionManager);
        this.authenticationManager = checkNotNull(authenticationManager);
        this.logger = logger;
    }

    @Override
    public Class<GetChapSessionAction> getActionClass() {
        return GetChapSessionAction.class;
    }

    @Override
    public RequestValidator<GetChapSessionAction> getRequestValidator(GetChapSessionAction action, RequestContext requestContext) {
        return NullValidator.get();
    }

    @Override
    public GetChapSessionResult execute(GetChapSessionAction action, ExecutionContext executionContext) {
        UserId userId = action.getUserId();
        if(userId.isGuest()) {
            logger.info("Attempt at authenticating guest user");
            return new GetChapSessionResult(Optional.<ChapSession>absent());
        }
        Optional<Salt> salt = authenticationManager.getSalt(userId);
        if(!salt.isPresent()) {
            return new GetChapSessionResult(Optional.<ChapSession>absent(), Optional.of(Boolean.TRUE));
        }
        ChapSession challengeMessage = chapSessionManager.getSession(salt.get());
        return new GetChapSessionResult(Optional.of(challengeMessage));
    }
}
