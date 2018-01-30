package br.com.modulo2.androidaccelerate.projeto_4.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import br.com.modulo2.androidaccelerate.projeto_4.R;

/**
 * Created by JoséLucas on 03/08/2017.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_geral);
        //Adiciona os arrays de Strings
        bindPreferenceSummary(findPreference(getString(R.string.prefs_ordem_key)));
        bindPreferenceSummary(findPreference(getString(R.string.prefs_idioma_key)));
    }

    //Método responsável por chamar o listner change
    private void bindPreferenceSummary(Preference preference){
        //Adiciona a propria classe no setOnPreferenceChangeListener já que estamos implementando isso
        preference.setOnPreferenceChangeListener(this);
        /**
         *Chamada para o metodo criado abaixo passando o preference e o novo valor
         * Para pegar o novo valor, utilizo o PreferenceManager.getSharedPreferences passando o contexto da propria preference
         * pego a String da preferencia com um preference.getKey() e passo também um valor vazio como padrão
         */
        onPreferenceChange( preference,
                PreferenceManager.getDefaultSharedPreferences(
                        preference.getContext() )
                        .getString( preference.getKey(), "") );

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // atribuindo a variável newValue para uma string, essa váriavel contém a nova configuração
        String valor = newValue.toString();
        //verifica se preference passado por parâmetro é uma instancia de ListPreference
        if(preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            //Procura a opção selecionada dentro da listagem e guarda o index dela
            int index = listPreference.findIndexOfValue(valor);
            //verifica se o valor encontrado é válido
            if(index >= 0){
                //Coloca a preferencia selecionada atravez do seu index no setSummary
                preference.setSummary(listPreference.getEntries()[index]);
            }
        }else{
            //Neste caso apenas colocamos o valor no setSummary
            preference.setSummary(valor);
        }

        return true;
    }
}
