package app

sealed trait AppError
case class DecodeFailure(message: String) extends AppError
