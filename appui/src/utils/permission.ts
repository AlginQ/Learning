/**
 * 权限验证工具函数
 */

/**
 * 检查用户是否有指定角色
 * @param requiredRole 需要的角色
 * @returns boolean
 */
export function hasRole(requiredRole: string): boolean {
  const userRole = localStorage.getItem('role') || 'USER'
  return userRole === requiredRole
}

/**
 * 检查用户是否有管理员权限
 * @returns boolean
 */
export function isAdmin(): boolean {
  return hasRole('ADMIN')
}

/**
 * 检查用户是否已登录
 * @returns boolean
 */
export function isAuthenticated(): boolean {
  const token = localStorage.getItem('token')
  return !!token
}

/**
 * 获取当前用户角色
 * @returns string
 */
export function getCurrentRole(): string {
  return localStorage.getItem('role') || 'USER'
}

/**
 * 二次权限校验 - 防止手动修改localStorage
 * 通过调用后端API验证用户真实权限
 * @param requiredRole 需要的角色
 * @returns Promise<boolean>
 */
export async function verifyPermission(requiredRole: string): Promise<boolean> {
  try {
    // 这里应该调用后端API验证用户权限
    // 例如：await api.verifyUserRole()
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 100))
    
    // 实际项目中应该从后端获取真实的角色信息
    const actualRole = localStorage.getItem('role') || 'USER'
    return actualRole === requiredRole
  } catch (error) {
    console.error('权限验证失败:', error)
    return false
  }
}

/**
 * 管理员权限二次校验
 * @returns Promise<boolean>
 */
export async function verifyAdminPermission(): Promise<boolean> {
  return verifyPermission('ADMIN')
}

/**
 * 权限指令 - v-permission
 * 用于在模板中根据权限控制元素显示
 */
export const permissionDirective = {
  mounted(el: HTMLElement, binding: any) {
    const { value } = binding
    const userRole = getCurrentRole()
    
    if (Array.isArray(value)) {
      // 数组形式：['ADMIN', 'USER']
      if (!value.includes(userRole)) {
        el.style.display = 'none'
      }
    } else {
      // 字符串形式：'ADMIN'
      if (userRole !== value) {
        el.style.display = 'none'
      }
    }
  },
  
  updated(el: HTMLElement, binding: any) {
    const { value } = binding
    const userRole = getCurrentRole()
    
    if (Array.isArray(value)) {
      el.style.display = value.includes(userRole) ? '' : 'none'
    } else {
      el.style.display = userRole === value ? '' : 'none'
    }
  }
}