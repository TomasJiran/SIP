/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import models.User;
import models.Student;
import play.data.*;
import play.mvc.*;
import views.html.other.*;
import static play.data.Form.*;
/**
 *
 * @author Dominik
 */
@Security.Authenticated(Secured.class)
public class OtherController extends Controller{
    
    final static Form<User> userForm = form(User.class);

    public static Result choose() {
        switch(User.find.byId(session("email")).userRole){
            case "admin": 
                return ok(choose.render(User.find.byId(session("email"))));
            case "student": 
                return ok(choose.render(User.find.byId(session("email"))));
                //return ok("Student tu zatím nic nemá.");
            case "teacher": 
                return ok(choose.render(User.find.byId(session("email"))));
            default:
                return ok("Neznámá uživatelská role.");           
        }
        
    }
    
    public static Result create() {
        if(User.find.byId(session("email")).userRole.equals("admin"))
        return ok(create.render(userForm, User.find.byId(session("email"))));
        else return ok("Přístupné pouze adminovi.");
    }
    public static Result showAll() {
        if(User.find.byId(session("email")).userRole.equals("admin"))
        return ok(showAll.render(User.find.all(), User.find.byId(session("email"))));
        else return ok("Přístupné pouze adminovi.");
    }
    public static Result showAllStudents() {
        if(User.find.byId(session("email")).userRole.equals("teacher"))
        return ok(showAllStudents.render(User.find.all(), User.find.byId(session("email"))));
        else return ok("Přístupné pouze učiteli.");
    }
    
    public static Result editPassword(){
        Form<User> prefilledForm=form(User.class).fill(User.find.byId(session("email")));
        
        return ok(editPassword.render(prefilledForm,User.find.byId(session("email"))));
    }
    
    public static Result add() {       
        Form<User> filledForm = userForm.bindFromRequest();       
        
        // Check repeated password
        if(!filledForm.field("password").valueOr("").isEmpty()) {
            if(!filledForm.field("password").valueOr("").equals(filledForm.field("repeatPassword").value())) {
                filledForm.reject("repeatPassword", "Hesla nejsou stejná.");
            }
        }        
        
        if(filledForm.hasErrors()) {
            return badRequest(create.render(filledForm, User.find.byId(session("email"))));
        } else {
            User user = filledForm.get();
            user.save();
            return ok(createSummary.render(user, User.find.byId(session("email"))));
        }
        
    }
    
    public static Result changePassword(String name,String email, String userRole){        
        Form<User> filledForm = userForm.bindFromRequest();
        
        if(!filledForm.field("password").valueOr("").isEmpty()) {
            if(!filledForm.field("password").valueOr("").equals(filledForm.field("repeatPassword").value())) {
                filledForm.reject("repeatPassword", "Hesla nejsou stejná.");
            }
        }
        
        if(filledForm.hasErrors()) {
            return badRequest(editPassword.render(filledForm, User.find.byId(session("email"))));
        } else {
            User user = filledForm.get();
            user.name=name;
            user.email=email;
            user.userRole=userRole;
            user.update();
            return ok(editPasswordSummary.render(user, User.find.byId(session("email"))));
        }
    }
    
}
