import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStore } from './auth.store';
import { UserRole } from './user-rol.enum';

export const anyRoleGuard: CanActivateFn = () => {
  const auth = inject(AuthStore);
  const router = inject(Router);

  const isAuthenticated = auth.isAuthenticatedValue();
  const role = auth.getRoleValue();

  if (
    !isAuthenticated ||
    (role !== UserRole.ADMIN && role !== UserRole.EMPLEADO)
  ) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
