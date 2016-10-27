package com.epam.redissessionaddon;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.promotionengineservices.promotionengine.impl.DefaultPromotionEngineService;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This enhanced promotion engine service is required because the out-the-box service
 * {@link DefaultPromotionEngineService} uses {@link Object} for session mutex and it adds this
 * plain object to session. Since Object is not {@link java.io.Serializable} this causes an exception in Redis
 * serialization process.
 *
 * My best idea was to use {@link ReentrantLock} object since it fulfills both requirement can be used as a mutex and
 * implements {@link java.io.Serializable} interface.
 *
 * TODO consider to replace it with AOP to avoid necessaty to override class.
 * TODO check if mutex in session works with session replication. I have suspecious that it doesn't
 */
public class EnhancedPromotionEngineService extends DefaultPromotionEngineService {
    @Override
    public PromotionOrderResults updatePromotions(Collection<PromotionGroupModel> promotionGroups, AbstractOrderModel order) {
        final ReentrantLock perSessionLock = this.getSessionService().getOrLoadAttribute("promotionsUpdateLock", () -> new ReentrantLock());
        try {
            perSessionLock.lock();
            return this.updatePromotionsNotThreadSafe(promotionGroups, order);
        } finally {
            perSessionLock.unlock();
        }
    }
}
