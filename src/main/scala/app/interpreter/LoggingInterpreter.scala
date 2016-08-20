package app.interpreter

import app.logging.AppLog

trait LoggingInterpreter {
  def log(appLog: AppLog): Unit
}