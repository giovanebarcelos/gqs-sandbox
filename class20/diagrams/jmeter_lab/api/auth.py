from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from pydantic import BaseModel

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/token")

# Usuários fixos de exemplo
fake_users = {
    "dickvigarista": {"username": "dickvigarista", "password": "12345"},
    "medinho": {"username": "medinho", "password": "12345"},
    "penelopecharmosa": {"username": "penelopecharmosa", "password": "12345"}
}

class User(BaseModel):
    username: str

def get_current_user(token: str = Depends(oauth2_scheme)):
    if token not in fake_users:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token inválido",
            headers={"WWW-Authenticate": "Bearer"},
        )
    return User(username=token)