## Android Screenshots

  Splash Activity                 |   Home        |  Meus Anúncios 
:-------------------------:|:-------------------------:|:-------------------------:
![](https://i.imgur.com/ViSPldw.jpg)|![](https://i.imgur.com/5Sgru8C.jpg)|![](https://i.imgur.com/4y3ngKV.jpg)

  Favoritos                 |   Minha Conta         |  Meu Endereço
:-------------------------:|:-------------------------:|:-------------------------:
![](https://i.imgur.com/4hCzGCZ.jpg)|![](https://i.imgur.com/N9MMtYG.jpg)|![](https://i.imgur.com/UkMS5o9.jpg)

  Anúncio                 |   Estados        |  Regiões
:-------------------------:|:-------------------------:|:-------------------------:
![](https://i.imgur.com/bLyhaDx.jpg)|![](https://i.imgur.com/fc40RVd.jpg)|![](https://i.imgur.com/zwwqn0X.jpg)

  Categorias                 |   Form Anúncio        |  Login
:-------------------------:|:-------------------------:|:-------------------------:
![](https://i.imgur.com/yUKsqKa.jpg)|![](https://i.imgur.com/fPfaoux.jpg)|![](https://i.imgur.com/6bAdqc3.jpg)

  Criar Conta                |   Recuperar Senha        |  Perfil
:-------------------------:|:-------------------------:|:-------------------------:
![](https://i.imgur.com/BhCTfjk.jpg)|![](https://i.imgur.com/ytHAZah.jpg)|![](https://i.imgur.com/5uiYdwo.jpg)

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
            |      │──AdapterGaleria
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
            |      └──Permissao
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
                  │──GetMask
                  │──RegiaoList
                  └──SPFiltro
```
