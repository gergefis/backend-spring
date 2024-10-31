Για να συνδέσεις το Spring Boot backend (με την ασφάλεια που έχεις διαμορφώσει στο UserConfig) με ένα Angular frontend (έκδοση 18.0.7), θα πρέπει να ακολουθήσεις τα παρακάτω βήματα για την ολοκλήρωση της σύνδεσης και της διαχείρισης της ασφάλειας (authentication) μέσω HTTP Basic authentication.

Βήματα για τη σύνδεση του Spring Boot backend με το Angular frontend
1. Ρύθμιση της βασικής αυθεντικοποίησης (HTTP Basic Authentication) στο Angular
Στο UserConfig, έχεις διαμορφώσει το http.httpBasic(Customizer.withDefaults());, που σημαίνει ότι η εφαρμογή σου χρησιμοποιεί HTTP Basic authentication για την ασφάλεια.

Το Angular θα πρέπει να αποστέλλει το όνομα χρήστη και τον κωδικό πρόσβασης στις αιτήσεις προς το Spring Boot backend. Το HTTP Basic authentication απαιτεί να προσθέτεις ένα authorization header σε κάθε αίτηση.

1.1 Δημιουργία ενός Authentication service στο Angular
Δημιούργησε μια υπηρεσία (service) στο Angular που θα χειρίζεται την αυθεντικοποίηση και την προσθήκη των απαραίτητων HTTP headers για την αποστολή αιτημάτων με πιστοποιήσεις.

bash
Copy code
ng generate service authentication
Στο authentication.service.ts:

typescript
Copy code
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private apiUrl = 'http://localhost:8080/api';  // URL του backend API

  constructor(private http: HttpClient) { }

  // Μέθοδος για το login
  authenticate(username: string, password: string): Observable<any> {
    const headers = new HttpHeaders({
      authorization: 'Basic ' + btoa(username + ':' + password)
    });

    return this.http.get(`${this.apiUrl}/user/getUsers`, { headers });
  }

  // Μπορείς να προσθέσεις περισσότερες μεθόδους για αιτήσεις CRUD που προστατεύονται
}
Χρησιμοποιούμε το btoa() για να κωδικοποιήσουμε το όνομα χρήστη και τον κωδικό πρόσβασης στη μορφή που απαιτείται για το Basic Authentication.
Προσθέτουμε τα credentials στο Authorization header σε κάθε αίτημα προς τον server.
1.2 Επεξεργασία της Angular εφαρμογής (login component)
Δημιούργησε ένα login component στο Angular, όπου οι χρήστες θα εισάγουν το όνομα χρήστη και τον κωδικό πρόσβασης.


Copy code
    **ng generate component login**
Σ       το login.component.ts:

typescript
Copy code
import { Component } from '@angular/core';
import { AuthenticationService } from '../authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  loginError: string = '';

  constructor(private authService: AuthenticationService) { }

  login() {
    this.authService.authenticate(this.username, this.password).subscribe({
      next: (data) => {
        console.log('Login successful', data);
        // Αποθήκευση των στοιχείων χρήστη (π.χ. session)
        this.loginError = '';
      },
      error: (err) => {
        console.error('Login failed', err);
        this.loginError = 'Login failed. Please check your credentials.';
      }
    });
  }
}
Στο login.component.html:

html
Copy code
<div class="login-container">
  <h2>Login</h2>
  <form (submit)="login()">
    <div>
      <label for="username">Username:</label>
      <input type="text" id="username" [(ngModel)]="username" required />
    </div>
    <div>
      <label for="password">Password:</label>
      <input type="password" id="password" [(ngModel)]="password" required />
    </div>
    <button type="submit">Login</button>
  </form>
  <p *ngIf="loginError" style="color:red">{{ loginError }}</p>
</div>
Αυτός ο κώδικας χειρίζεται το login. Όταν ο χρήστης υποβάλει τα στοιχεία του, η εφαρμογή κάνει αίτηση στο backend με Basic authentication.

1.3 Ρύθμιση της Angular για διασύνδεση με το backend
Για να εξασφαλίσεις ότι το Angular frontend μπορεί να επικοινωνεί με το Spring Boot backend, πρέπει να επιτρέψεις CORS (Cross-Origin Resource Sharing) στο Spring Boot backend, επειδή τα frontend και backend σου πιθανότατα τρέχουν σε διαφορετικά domains ή θύρες.

Μπορείς να ρυθμίσεις το CORS στο Spring Boot όπως φαίνεται παρακάτω:

java
Copy code
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") // Ρύθμιση για το Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
Αυτό επιτρέπει στο Angular frontend (που τρέχει στο http://localhost:4200) να στέλνει αιτήσεις στο Spring Boot backend.

1.4 Εγκατάσταση του HttpClientModule στο Angular
Για να μπορέσει το Angular να στέλνει HTTP αιτήσεις, βεβαιώσου ότι έχεις εισάγει το HttpClientModule στο app.module.ts.

typescript
Copy code
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule  // Προσθήκη του HttpClientModule για HTTP αιτήσεις
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
2. Ρύθμιση διαδρομών στο Angular
Αφού ολοκληρωθεί το login, μπορείς να δημιουργήσεις περισσότερα components για προστατευμένες σελίδες (π.χ. λίστα χρηστών, δημιουργία χρήστη κ.λπ.) και να διαχειρίζεσαι την πλοήγηση.

Μπορείς να χρησιμοποιήσεις τον Router για την πλοήγηση σε προστατευμένες διαδρομές μετά το επιτυχές login. Ακόμη, μπορείς να προσθέσεις Route Guards για την προστασία συγκεκριμένων διαδρομών.

Συμπερασματικά:
Στο backend, χρησιμοποίησες Basic authentication για να προστατέψεις τις διαδρομές μέσω του UserConfig.
Στο Angular frontend, δημιουργείς ένα authentication service που αποστέλλει τα credentials μέσω HTTP Basic Authentication στις αιτήσεις προς το Spring Boot backend.
Προσθέτεις CORS στο Spring Boot για να επιτρέψεις αιτήσεις από το Angular frontend.
Με αυτήν την αρχιτεκτονική, το Angular frontend μπορεί να επικοινωνεί με το Spring Boot backend, διασφαλίζοντας ότι οι αιτήσεις προστατεύονται μέσω HTTP Basic authentication.