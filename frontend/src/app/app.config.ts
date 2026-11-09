import { ApplicationConfig, LOCALE_ID, importProvidersFrom, provideBrowserGlobalErrorListeners } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter, withComponentInputBinding, withViewTransitions } from '@angular/router';
import {
  Ban, Bell, Building2, CalendarDays, CalendarPlus, ChartColumn, Check, ChevronRight, CircleCheck, ClipboardCheck,
  Clock, DoorOpen, Eye, EyeOff, GraduationCap, History, Hourglass, Inbox, Info, LayoutDashboard, LoaderCircle, LogOut,
  LucideAngularModule, MapPin, Menu, Package, PackageCheck, PackageOpen, Pencil, Play, Plus, Power, Search,
  ShieldCheck, Sparkles, Square, Trash2, TriangleAlert, UserCog, Users, Wrench, X
} from 'lucide-angular';
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor';

registerLocaleData(localePt);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding(), withViewTransitions()),
    provideHttpClient(withInterceptors([authInterceptor])),
    { provide: LOCALE_ID, useValue: 'pt-BR' },
    importProvidersFrom(
      LucideAngularModule.pick({
        Ban, Bell, Building2, CalendarDays, CalendarPlus, ChartColumn, Check, ChevronRight, CircleCheck, ClipboardCheck,
        Clock, DoorOpen, Eye, EyeOff, GraduationCap, History, Hourglass, Inbox, Info, LayoutDashboard, LoaderCircle,
        LogOut, MapPin, Menu, Package, PackageCheck, PackageOpen, Pencil, Play, Plus, Power, Search, ShieldCheck,
        Sparkles, Square, Trash2, TriangleAlert, UserCog, Users, Wrench, X
      })
    )
  ]
};
