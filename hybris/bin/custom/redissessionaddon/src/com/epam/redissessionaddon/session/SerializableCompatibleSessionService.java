package com.epam.redissessionaddon.session;

import com.google.common.base.Preconditions;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.io.Serializable;

/**
 * Created by igorsokolov on 10/29/16.
 */
public class SerializableCompatibleSessionService extends DefaultSessionService {
    @Override
    public void setAttribute(String name, Object value) {
        Preconditions.checkArgument(value instanceof Serializable, "Session attribute should be serializable.");
        super.setAttribute(name, value);
    }

    @Override
    public <T> T getOrLoadAttribute(String name, SessionAttributeLoader<T> loader) {
        ServicesUtil.validateParameterNotNullStandardMessage("loader", loader);
        JaloSession currentSession = JaloSession.getCurrentSession();

        T result = this.getAttribute(name);
        if (result == null) {
            synchronized(currentSession) {
                result = this.getAttribute(name);
                if(result == null) {
                    result = loader.load();

                    if (Object.class.equals(result.getClass())) {
                        this.setAttribute(name, new Mutex());
                    } else {
                        this.setAttribute(name, result);
                    }
                }
            }
        }

        return result;
    }

    /**
     * The mutex to be registered.
     * Doesn't need to be anything but a plain Object to synchronize on.
     * Should be serializable to allow for session persistence.
     */
    @SuppressWarnings("serial")
    private static class Mutex implements Serializable {
    }
}
