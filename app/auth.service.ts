import { Injectable }      from '@angular/core';
import { tokenNotExpired } from 'angular2-jwt';
import { myConfig }        from './auth.config';

// Avoid name not found warnings
declare var Auth0Lock: any;

@Injectable()
export class Auth {
  // Configure Auth0
  lock = new Auth0Lock('TJSO4rVtfO9NehWy3YrmSWXNQEXIqZQK', 'app58285542.auth0.com', {
	theme: {
		logo: "../CO2.png",
		primaryColor: "#b81b1c"	
	},
	languageDictionary: {
		title: "CO2 & You"
	},
	auth: {
     		redirectUrl: 'https://co2-you.herokuapp.com/callback',
      		responseType: 'code',
      		params: {
        		scope: 'openid email' // Learn about scopes: https://auth0.com/docs/scopes
      		}
    	}
   });

  constructor() {
    // Add callback for lock `authenticated` event
    this.lock.on('authenticated', (authResult) => {
      localStorage.setItem('id_token', authResult.idToken);
    });

    this.lock.show();
  }

  public login() {
    // Call the show method to display the widget.
    this.lock.show();
  };

  public authenticated() {
    // Check if there's an unexpired JWT
    // It searches for an item in localStorage with key == 'id_token'
    return tokenNotExpired();
  };

  public logout() {
    // Remove token from localStorage
    localStorage.removeItem('id_token');
  };
}
