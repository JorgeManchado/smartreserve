import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStore } from './auth.store';
import { UserRole } from './user-rol.enum';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthStore);
  const router = inject(Router);

  if (!auth.isAuthenticatedValue() || auth.getRoleValue() !== UserRole.ADMIN) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
