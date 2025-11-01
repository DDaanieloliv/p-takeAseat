export interface FieldError {
  field: 'name' | 'cpf';
  message: string;
  inputElement?: HTMLInputElement;
}
