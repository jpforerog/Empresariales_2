LISTAR TODAS LAS ARMAS
GET http://localhost:8080/Arma/

LISTAR POR TIPO
GET http://localhost:8080/Arma/tipo
{
    "tipo" : "Rifle"
}

LISTAR POR VIDA MINIMA
GET http://localhost:8080/Arma/vida
{
    "vida minima" : 2
}

INSERTAR UN ARMA
POST http://localhost:8080/Arma/
{
  "nombre": "hola1",
  "daño": 2,
  "municion": 2,
  "vida": 2,
  "velocidad": 2,
  "fechaCreacion":"2025-03-28T00:00:10"
}

ELIMINAR UN ARMA
DELETE http://localhost:8080/Arma/
{
    "nombre":"hola",
    "tipo":"Rifle"
}

EDITAR UN ARMA
PUT http://localhost:8080/Arma/
{
  "nombre": "hola",
  "daño": 2,
  "municion": 2,
  "vida": 6,
  "velocidad": 2,
  "fechaCreacion":"2025-03-28T00:00:10",
  "nombreNuevo":"hola1",
  "tipo":"Rifle"
}