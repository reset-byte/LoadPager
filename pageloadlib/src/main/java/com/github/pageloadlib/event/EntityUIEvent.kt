package com.github.pageloadlib.event

/**
 * 用于通知UI层的事件:加载更多失败，加载完成，加载中
 */
class EntityUIEvent(var eventType: EventType) {
    enum class EventType {
        LOAD_MORE_FAILED, LOAD_FINISH, LOADING
    }
}