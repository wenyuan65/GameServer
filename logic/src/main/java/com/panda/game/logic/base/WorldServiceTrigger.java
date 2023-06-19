package com.panda.game.logic.base;

import com.panda.game.logic.world.WorldModuleService;

/**
 * 公共服务触发点
 */
public enum WorldServiceTrigger {

    init {
        @Override
        public boolean trigger(WorldModuleService service, Object... params) {
            return service.init();
        }
    },
    loadDatabase {
        @Override
        public boolean trigger(WorldModuleService service, Object... params) {
            return service.loadDatabase();
        }
    },
    save {
        @Override
        public boolean trigger(WorldModuleService service, Object... params) {
            return service.save();
        }
    },
    resetDaily {
        @Override
        public boolean trigger(WorldModuleService service, Object... params) {
            return service.resetDaily();
        }
    },
    tick {
        @Override
        public boolean trigger(WorldModuleService service, Object... params) {
            service.tick((Long) params[0]);
            return true;
        }
    },
    ;

    public abstract boolean trigger(WorldModuleService service, Object... params);
}
