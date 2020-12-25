## Android Screenshots

  Splash                 |   Home        |  Anúncio
:-------------------------:|:-------------------------:|:-------------------------:
![](https://i.imgur.com/ffK3mlX.jpg)|![](https://i.imgur.com/Iu2RsEq.jpg)|![](https://i.imgur.com/CRi7Sy3.jpg)

  Estados                 |   Região         |  Categorias
:-------------------------:|:-------------------------:|:-------------------------:
![](https://i.imgur.com/THGLW8a.jpg)|![](https://i.imgur.com/aBrhlYl.jpg)|![](https://i.imgur.com/QHwGAHt.jpg)

  Filtro                 |   Meus Anúncios        |  Favoritos
:-------------------------:|:-------------------------:|:-------------------------:
![](https://i.imgur.com/EGTIBbD.jpg)|![](https://i.imgur.com/mmxlSO7.jpg)|![](https://i.imgur.com/4TpUL2A.jpg)

  Minha Conta                 |   Perfil        |  Meu Endereço
:-------------------------:|:-------------------------:|:-------------------------:
![](https://i.imgur.com/Ww4H7xh.jpg)|![](https://i.imgur.com/RfM9Mtf.jpg)|![](https://i.imgur.com/L18ZrSC.jpg)

  Login                |   Criar Conta        |  Recuperar Senha
:-------------------------:|:-------------------------:|:-------------------------:
![](https://i.imgur.com/Z8zHOpN.jpg)|![](https://i.imgur.com/eRhr69p.jpg)|![](https://i.imgur.com/fbC5F1f.jpg)

### Download APK
[Download](https://github.com/ArleyPereira/Delivery/blob/master/Delivery.apk)

## Directory Structure
```
app
└───java
    └───com.example.olxclone
            |
            │───activity
            │      │──CategoriasActivity
            |      │──DetalheAnuncioActivity
            |      │──EstadosActivity
            │      │──FiltrosActivity
            |      │──FormAnuncioActivity
            |      │──FormEnderecoActivity
            │      │──ImagemZoomAnuncioActivity
            |      │──MainActivity
            |      │──PerfilActivity
            |      │──RegioesActivity
            |      |──SplashActivity
            |      |
            |      └─────autenticacao
            │                 │──CriarContaActivity
            │                 │──LoginActivity
            │                 └──RecuperarSenhaActivity
            |
            │───adapter
            |      │──AdapterAnuncio
            |      │──AdapterCategoria
            |      │──AdapterEstado
            |      │──AdapterRegiao
            |      └──SliderAdapter
            |
            |───api
            |      └──CEPService
            |
            |───fragment
            |      │──ContaFragment
            |      │──FavoritosFragment
            |      │──HomeFragment
            |      └──MeusAnunciosFragment
            |
            |───helper
            |      │──GetFirebase
            |      └──Preferencia
            |
            |───model
            |     │──Anuncio
            |     │──Categoria
            |     │──Endereco
            |     │──Estado
            |     │──Favorito
            |     │──Filtro
            |     │──Imagem
            |     │──Local
            |     └──Usuario
            |
            └───Util
                  │──CategoriaList
                  │──EstadoList
                  │──GetMask
                  │──RegiaoList
                  └──SPFiltro
```
