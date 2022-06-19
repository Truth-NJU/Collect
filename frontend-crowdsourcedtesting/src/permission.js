import router from './router'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import getPageTitle from '@/utils/get-page-title'
import { getIdentity } from '@/utils/auth'

NProgress.configure({ showSpinner: false }) // NProgress Configuration

router.beforeEach(async(to, from, next) => {
  // start progress bar
  NProgress.start()

  // set page title
  document.title = getPageTitle(to.meta.title)

  const identity = getIdentity()
  const name = to.name
  if (name === 'myTask' || name === 'taskForm' || name === 'reports' || name === 'Form' || name === 'admin' || name === 'reportDetail') {
    if (to.meta[identity]) {
      next()
    } else { next('/404') }
  }

  next()
})

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})
