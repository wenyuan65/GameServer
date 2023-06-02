package com.panda.game.logic.base;

public enum ServiceTrigger {

    save {
        @Override
        public boolean trigger(ModuleService service, Object... params) {
            return service.save();
        }
    },

    unload {
        @Override
        public boolean trigger(ModuleService service, Object... params) {
            return service.unload();
        }
    },

    doAfterLoaded {
        @Override
        public boolean trigger(ModuleService service, Object... params) {
            return service.doAfterLoaded();
        }
    },

    doBeforeLogin {
        @Override
        public boolean trigger(ModuleService service, Object... params) {
            return service.doBeforeLogin();
        }
    },

    doAfterLogin {
        @Override
        public boolean trigger(ModuleService service, Object... params) {
            return service.doAfterLogin();
        }
    },

    doAfterLogout {
        @Override
        public boolean trigger(ModuleService service, Object... params) {
            return service.doAfterLogout();
        }
    },

    resetDaily {
        @Override
        public boolean trigger(ModuleService service, Object... params) {
            service.resetDaily((Boolean) params[0]);
            return true;
        }
    },

    resetBySelf {
        @Override
        public boolean trigger(ModuleService service, Object... params) {
            service.resetBySelf((Boolean) params[0]);
            return true;
        }
    },
    ;

    public abstract boolean trigger(ModuleService service, Object... params);


}
