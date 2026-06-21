export const environment = {
  production: false,

  cognitoUserPoolId: '',
  cognitoAppClientId: '',

  // En dev (ng serve), on appelle le backend directement sans passer par nginx
  apiUrl: 'http://localhost:8080',
};
