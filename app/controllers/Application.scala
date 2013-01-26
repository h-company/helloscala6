package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import jp.t2v.lab.play20.auth._

object Application extends Controller  with LoginLogout with AuthConfigImple with Auth{
  type LoginForm = Form[(String, String)]

  def index = authorizedAction(models.Normal) { user => request =>
    Ok(views.html.index(user.fullname))
  }

  def login = Action {
    Ok(views.html.login(loginForm))
  }

  val loginForm: LoginForm = Form {
    tuple("email" -> email, "password" -> nonEmptyText)
  }


  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
    formWithErrors => BadRequest(views.html.login(formWithErrors)),
    {
      case (email, password) =>
        models.User.authenticate(email, password)
          .map(_.id)
          .map(gotoLoginSucceeded)
          .getOrElse(
          BadRequest(views.html.login(loginForm.fill((email, "")).withGlobalError("emailもしくはパスワードが間違っています。"))))
    }
    )
  }

  def logout = Action { implicit request =>
    gotoLogoutSucceeded
  }
}