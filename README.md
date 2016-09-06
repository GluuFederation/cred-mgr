# credmgr

This application was generated using JHipster, you can find documentation and help at [https://jhipster.github.io](https://jhipster.github.io).

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools (like
[Bower][] and [BrowserSync][]). You will only need to run this command when dependencies change in package.json.

    npm install

We use [Gulp][] as our build system. Install the Gulp command-line tool globally with:

    npm install -g gulp

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./mvnw
    gulp

Bower is used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in `bower.json`. You can also run `bower update` and `bower install` to manage dependencies.
Add the `-h` flag on any command to see how you can use it. For example, `bower update -h`.


## Building for production

To optimize the credmgr client for production, run:

    ./mvnw -Pprod clean package

This will concatenate and minify CSS and JavaScript files. It will also modify `index.html` so it references
these new files.

To ensure everything worked, run:

    java -jar target/*.war --spring.profiles.active=prod

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

## Testing

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in `src/test/javascript/` and can be run with:

    gulp test



## Continuous Integration

To setup this project in Jenkins, use the following configuration:

* Project name: `credmgr`
* Source Code Management
    * Git Repository: `git@github.com:xxxx/credmgr.git`
    * Branches to build: `*/master`
    * Additional Behaviours: `Wipe out repository & force clone`
* Build Triggers
    * Poll SCM / Schedule: `H/5 * * * *`
* Build
    * Invoke Maven / Tasks: `-Pprod clean package`
* Post-build Actions
    * Publish JUnit test result report / Test Report XMLs: `build/test-results/*.xml`

[JHipster]: https://jhipster.github.io/
[Node.js]: https://nodejs.org/
[Bower]: http://bower.io/
[Gulp]: http://gulpjs.com/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/

## Configuring cred-mgr

Please follow these steps to configure your `cred-mgr` app and your `gluu` server:

1. export ssl certificate from chrome (e.g. `gluu.localhost.info.cer`).

2. remove old imported certificate from java keystore: `sudo keytool -delete -noprompt -alias gluu.localhost.info  -keystore cacerts -storepass changeit`.

3. add new certificate to java keystore: `sudo keytool -import -alias gluu.localhost.info -file gluu.localhost.info.cer -keystore cacerts -storepass changeit`.

4. create folder for jks storage using path/name provided from `credmgr.jksStorePath` property (`cred-mgr/src/main/resources/config/application-dev.yml`), then inside created folder create new one with name `gluu` and put there your jks file from `/opt/gluu-server-2.4.4/install/community-edition-setup/output/scim-rp.jks`. So you'll have `${credmgr.jksStorePath}/gluu/scim-rp.jks`.

5. replace property `umaAatClientId` from `cred-mgr/src/main/resources/config/application-dev.yml`  and `cred-mgr/src/test/resources/config/application.yml` with one from `/install/community-edition-setup/setup.properties.last` e.g. `cat setup.properties.last | grep "scim_rp_client_id"`.

6. drop, then create `credmgr` database.

7. run credmgr app.

8. login into Gluu Server(oxTrust CE) and navigate to Configuration > Organization Configuration > Enable SCIM Support.

9. Navigate to `https://${your.gluu.server.host.name}/oxauth-rp/home.htm` and register client with the following params:
    * Registration Endpoint: `https:/${your.gluu.server.host.name}/oxauth/seam/resource/restv1/oxauth/register`
    * Redirect URIs: `https://127.0.0.1:9000/api/openid/login-redirect`
    * Post Logout Redirect URIs: `https://127.0.0.1:9000/api/openid/logout-redirect`
    * Response Types: `CODE`
    * Grant Types: `AUTHORIZATION_CODE`
    * Application Type: `WEB`

10. Copy client_id and client_secret from Registration Response panel and update first record from op_config table e.g
`UPDATE op_config SET client_id=‘your_client_id’, client_secret='your_client_secret’, host='https://your.gluu.server.host.name.without.last.slash’, email='gluu@mail.com', client_jks='/gluu/scim-rp.jks' WHERE id=1;`

11. Navigate to `https://${your.gluu.server.host.name}/identity/attribute/inventory` and add new Attributes:
    * opRole
        * Name: opRole
        * DisplayName: opRole
        * Type: Text
        * Multivalued: False
        * oxAuth claim name: opRole
        * SCIM Atribute: True
        * Description: opRole
    * resetDate
        * Name: resetDate
        * DisplayName: resetDate
        * Type: Text
        * Multivalued: False
        * oxAuth claim name: resetDate
        * SCIM Atribute: True
        * Description: resetDate
    * resetKey
        * Name: resetKey
        * DisplayName: resetKey
        * Type: Text
        * Multivalued: False
        * oxAuth claim name: resetKey
        * SCIM Atribute: True
        * Description: resetKey
    * resetPhoneNumber
        * Name: resetPhoneNumber
        * DisplayName: resetPhoneNumber
        * Type: Text
        * Multivalued: False
        * oxAuth claim name: resetPhoneNumber
        * SCIM Atribute: True
        * Description: resetPhoneNumber

12. Go to your project directory, open terminal, type: `gulp` and hit enter.

13. Important: navigate to `https://127.0.0.1:9000/#/` instead of `https://localhost:9000/#/`.

14. Now you can log in into cred-mgr app, create new admins and reset your credentials.
