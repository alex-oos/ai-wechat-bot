import { createRouter, createWebHistory } from 'vue-router'

import LoginView from './views/LoginView.vue'
import WechatLoginView from './views/WechatLoginView.vue'
import ChannelConfigView from './views/ChannelConfigView.vue'
import BotSettingsView from './views/BotSettingsView.vue'
import AiConfigView from './views/AiConfigView.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/wechat-login' },
    { path: '/login', component: LoginView, meta: { title: '登录' } },
    { path: '/wechat-login', component: WechatLoginView, meta: { title: '扫码登录' } },
    { path: '/config/channel', component: ChannelConfigView, meta: { title: '微信接入渠道' } },
    { path: '/config/bot', component: BotSettingsView, meta: { title: '机器人配置' } },
    { path: '/config/ai', component: AiConfigView, meta: { title: 'AI 配置' } },
  ],
})
